package com.example.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preference.PreferencesManager
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.UserProfile
import com.example.data.supabase.UserSubscription
import com.example.data.supabase.UserDocument
import com.example.data.supabase.UserApplication
import com.example.data.supabase.ReferralRecord
import com.example.util.CacheUtils.formatRelativeTime
import com.example.util.CacheUtils.parseMockJobList
import com.example.util.CacheUtils.parseUserApplicationList
import com.example.util.CacheUtils.parseUserProfile
import com.example.util.CacheUtils.toJsonString
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class OfflineBannerState {
    HIDDEN,
    OFFLINE,
    BACK_ONLINE
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)
    private val client = SupabaseClient(application)

    companion object {
        private const val TAG = "AppViewModel"
    }

    // UI States
    var currentTab by mutableStateOf("home")
    var activeApplyFlowJob by mutableStateOf<MockJob?>(null)
    var showNotificationExplanationDialog by mutableStateOf(false)
    var showUpgradePrompt by mutableStateOf(false)
    var globalJobDetailToShow by mutableStateOf<MockJob?>(null)
    var profileSubScreen by mutableStateOf("main") // "main", "completion_flow", "document_vault", "subscription_comparison"
    var dailyJobDetailViews by mutableStateOf(0)
    var showPremiumBannerToday by mutableStateOf(false)
    var isPremiumBannerDismissedToday by mutableStateOf(false)
    
    private val _themeMode = MutableStateFlow(prefs.themePreference) // "light", "dark", "system"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(prefs.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _userSubscription = MutableStateFlow<UserSubscription?>(null)
    val userSubscription: StateFlow<UserSubscription?> = _userSubscription.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _userDocuments = MutableStateFlow<List<UserDocument>>(emptyList())
    val userDocuments: StateFlow<List<UserDocument>> = _userDocuments.asStateFlow()

    private val _userApplications = MutableStateFlow<List<UserApplication>>(emptyList())
    val userApplications: StateFlow<List<UserApplication>> = _userApplications.asStateFlow()

    // Referral Program, Services Popup & Onboarding state
    private val _pendingReferralCode = MutableStateFlow<String?>(prefs.pendingReferralCode)
    val pendingReferralCode: StateFlow<String?> = _pendingReferralCode.asStateFlow()

    private val _userReferrals = MutableStateFlow<List<ReferralRecord>>(emptyList())
    val userReferrals: StateFlow<List<ReferralRecord>> = _userReferrals.asStateFlow()

    private val _jobViewsInSession = MutableStateFlow(0)

    private val _shouldShowServicesPopup = MutableStateFlow(false)
    val shouldShowServicesPopup: StateFlow<Boolean> = _shouldShowServicesPopup.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(prefs.hasCompletedOnboarding)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // Network & Offline Caching State
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _offlineBannerState = MutableStateFlow(OfflineBannerState.HIDDEN)
    val offlineBannerState: StateFlow<OfflineBannerState> = _offlineBannerState.asStateFlow()

    private val _lastCacheFormattedTime = MutableStateFlow(
        formatRelativeTime(if (prefs.cachedJobsTimestamp > 0) prefs.cachedJobsTimestamp else System.currentTimeMillis())
    )
    val lastCacheFormattedTime: StateFlow<String> = _lastCacheFormattedTime.asStateFlow()

    // Flag indicating Supabase status
    val isRealSupabaseConnected: Boolean = client.isRealConfigActive

    // Job states
    private val _jobs = MutableStateFlow<List<MockJob>>(
        listOf(
            MockJob(
                id = "1",
                title = "Software Engineer",
                organization = "LS Services IT Division",
                location = "Kampala",
                jobType = "Full-time",
                category = "Engineering & IT",
                salary = "UGX 3.5M - 4.5M",
                deadline = "2026-07-23",
                purpose = "To lead the development of enterprise web applications and maintain API integrations for local businesses.",
                requirements = "• 3+ years experience with Kotlin or Java\n• Proficiency with Spring Boot or Node.js\n• Experience with relational databases like PostgreSQL\n• Good team player with communication skills",
                otherDetails = "Office located in Nakasero, Kampala. Free lunch and medical insurance included.",
                opensExternally = false,
                officialLink = "https://lsrecruitingservices.com/jobs/software-engineer",
                applicationMethod = "auto_apply",
                viewsCount = 142,
                createdAt = "2026-07-21T09:00:00Z"
            ),
            MockJob(
                id = "2",
                title = "Clinical Research Nurse",
                organization = "Mbarara Regional Hospital",
                location = "Mbarara",
                jobType = "Full-time",
                category = "Healthcare",
                salary = "UGX 2.2M - 3.0M",
                deadline = "2026-07-24",
                purpose = "To coordinate clinical trials, gather participant data, and ensure compliance with medical protocols.",
                requirements = "• Registered Nurse with valid Uganda Nurses and Midwives Council license\n• 2+ years of clinical research experience\n• High attention to detail and record keeping\n• Knowledge of GCP (Good Clinical Practice) guidelines",
                otherDetails = "This is a 2-year renewable contract position based in Mbarara city.",
                opensExternally = true,
                officialLink = "https://mbararahospital.go.ug/careers/nurse-researcher",
                applicationMethod = "requires_personal_account",
                viewsCount = 89,
                createdAt = "2026-07-21T08:00:00Z"
            ),
            MockJob(
                id = "3",
                title = "Regional Marketing Officer",
                organization = "Nile Breweries",
                location = "Jinja",
                jobType = "Contract",
                category = "Sales & Marketing",
                salary = "UGX 1.8M - 2.5M",
                deadline = "2026-07-31",
                purpose = "To spearhead promotional campaigns, direct local distribution channels, and scale sales volume in the Eastern region.",
                requirements = "• Bachelor's Degree in Marketing, Business Administration or related\n• Proven track record of field sales success in Eastern Uganda\n• Ability to speak Lusoga and Luganda fluently\n• Valid driving permit is mandatory",
                otherDetails = "Field vehicle, fuel allowance, and generous commissions are provided.",
                opensExternally = false,
                officialLink = "https://nilebreweries.com/jobs/marketing-officer",
                applicationMethod = "auto_apply",
                viewsCount = 215,
                createdAt = "2026-07-20T14:30:00Z"
            ),
            MockJob(
                id = "4",
                title = "Assistant IT Lecturer",
                organization = "Makerere University",
                location = "Kampala",
                jobType = "Part-time",
                category = "Education",
                salary = "UGX 1.5M - 2.0M",
                deadline = "2026-08-15",
                purpose = "To conduct undergraduate labs, grade assignments, and tutor students in database design and mobile programming.",
                requirements = "• Master's Degree in Computer Science or Information Technology\n• Previous teaching assistant or mentoring experience\n• Deep expertise in SQL and Android SDK\n• Dedicated and passionate about teaching",
                otherDetails = "Teaching hours are flexible, mainly evening and weekend classes.",
                opensExternally = true,
                officialLink = "https://mak.ac.ug/jobs/assistant-lecturer-it",
                applicationMethod = "requires_personal_account",
                viewsCount = 310,
                createdAt = "2026-07-19T10:00:00Z"
            ),
            MockJob(
                id = "5",
                title = "Senior Accountant",
                organization = "Stanbic Bank Uganda",
                location = "Entebbe",
                jobType = "Full-time",
                category = "Finance",
                salary = "UGX 4.0M - 5.5M",
                deadline = "2026-08-01",
                purpose = "To oversee financial reporting, prepare tax computations, and coordinate internal and external audit operations.",
                requirements = "• CPA Uganda or ACCA fully qualified\n• 5+ years of banking or corporate accounting experience\n• Advanced knowledge of IFRS standards and Excel auditing\n• Integrity-driven and analytical",
                otherDetails = "Based at our Entebbe Main Branch. Comprehensive banking benefits package included.",
                opensExternally = false,
                officialLink = "https://stanbicbank.co.ug/careers/senior-accountant",
                applicationMethod = "auto_apply",
                viewsCount = 184,
                createdAt = "2026-07-18T11:00:00Z"
            ),
            MockJob(
                id = "6",
                title = "Mobile App Developer (Kotlin)",
                organization = "LS Services Tech",
                location = "Remote",
                jobType = "Remote",
                category = "Engineering & IT",
                salary = "UGX 3.0M - 4.0M",
                deadline = "2026-07-29",
                purpose = "To refine and build native Android application features using Jetpack Compose, Coroutines, and MVVM patterns.",
                requirements = "• Strong expertise in native Android development with Kotlin\n• Deep understanding of Jetpack Compose and modern architecture components\n• Experience consuming RESTful APIs and Supabase integrations\n• Independent, self-directed working style",
                otherDetails = "100% remote job with monthly internet stipends and learning allowances.",
                opensExternally = false,
                officialLink = "https://lsrecruitingservices.com/jobs/kotlin-developer",
                applicationMethod = "auto_apply",
                viewsCount = 420,
                createdAt = "2026-07-17T16:00:00Z"
            ),
            MockJob(
                id = "7",
                title = "Graduate Finance Intern",
                organization = "Centenary Bank",
                location = "Kampala",
                jobType = "Internship",
                category = "Finance",
                salary = "UGX 600K - 800K",
                deadline = "2026-07-22",
                purpose = "To assist the treasury team with daily reconciliations, data entry, and report drafting.",
                requirements = "• Recent graduate with a first-class or second-class upper degree in Finance or Economics\n• Basic knowledge of accounting principles\n• Eager to learn and highly motivated\n• Good team player",
                otherDetails = "This is a 6-month internship with potential for full-time conversion based on performance.",
                opensExternally = false,
                officialLink = "https://centenarybank.co.ug/jobs/finance-intern",
                applicationMethod = "auto_apply",
                viewsCount = 95,
                createdAt = "2026-07-15T09:00:00Z"
            ),
            MockJob(
                id = "8",
                title = "Agricultural Officer (Expired Demo)",
                organization = "Kasese District LG",
                location = "Remote",
                jobType = "Full-time",
                category = "Healthcare",
                salary = "UGX 1.2M - 1.5M",
                deadline = "2026-07-19",
                purpose = "To coordinate farming programs and advise farmers on modern crop management.",
                requirements = "• Degree in Agriculture or related field\n• Familiarity with local farming challenges\n• Willing to work in rural areas",
                otherDetails = "Government pension scheme included.",
                opensExternally = false,
                officialLink = "https://kasese.go.ug/jobs/agricultural-officer",
                applicationMethod = "auto_apply",
                viewsCount = 38,
                createdAt = "2026-07-10T12:00:00Z",
                status = "active"
            )
        ).sortedByDescending { it.createdAt }
    )
    val jobs: StateFlow<List<MockJob>> = _jobs.asStateFlow()

    // Bookmarks state
    private val _bookmarks = MutableStateFlow<Set<String>>(prefs.guestBookmarks)
    val bookmarks: StateFlow<Set<String>> = _bookmarks.asStateFlow()

    // Reported jobs
    private val _reportedJobs = MutableStateFlow<Set<String>>(prefs.reportedJobs)
    val reportedJobs: StateFlow<Set<String>> = _reportedJobs.asStateFlow()

    // Applied jobs
    private val _appliedJobs = MutableStateFlow<Set<String>>(prefs.appliedJobs)
    val appliedJobs: StateFlow<Set<String>> = _appliedJobs.asStateFlow()

    // Admin classification dynamic options
    private val _locations = MutableStateFlow<List<String>>(
        listOf("Kampala", "Mbarara", "Jinja", "Entebbe", "Gulu", "Fort Portal", "Kasese", "Arua", "Remote")
    )
    val locations: StateFlow<List<String>> = _locations.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(
        listOf("Engineering & IT", "Healthcare", "Sales & Marketing", "Education", "Finance", "Administration", "Human Resources", "Agriculture")
    )
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _jobTypes = MutableStateFlow<List<String>>(
        listOf("Full-time", "Part-time", "Contract", "Remote", "Internship", "Volunteer")
    )
    val jobTypes: StateFlow<List<String>> = _jobTypes.asStateFlow()

    // Admin Applications Queue across all users
    private val _allApplications = MutableStateFlow<List<UserApplication>>(
        listOf(
            UserApplication(
                id = "app_101",
                userId = "user_001",
                jobId = "1",
                generatedCvUrl = "https://mock-supabase.co/storage/v1/object/public/generated-cvs/patrick-cv.pdf",
                documentsAttached = listOf("National ID", "Academic Transcript"),
                status = "pending",
                appliedAt = "2026-07-21T11:30:00Z",
                jobTitle = "Software Engineer",
                jobOrganization = "LS Services IT Division",
                candidateName = "Patrick Roman",
                candidateEmail = "patrickromanug@gmail.com",
                candidatePhone = "+256 771 234 567",
                adminNotes = null
            ),
            UserApplication(
                id = "app_102",
                userId = "user_002",
                jobId = "2",
                generatedCvUrl = "https://mock-supabase.co/storage/v1/object/public/generated-cvs/sarah-cv.pdf",
                documentsAttached = listOf("Nursing License", "Passport Photo"),
                status = "pending",
                appliedAt = "2026-07-21T10:15:00Z",
                jobTitle = "Clinical Research Nurse",
                jobOrganization = "Mbarara Regional Hospital",
                candidateName = "Sarah Akello",
                candidateEmail = "sarah.akello@gmail.com",
                candidatePhone = "+256 782 990 112",
                adminNotes = null
            ),
            UserApplication(
                id = "app_103",
                userId = "user_003",
                jobId = "3",
                generatedCvUrl = "https://mock-supabase.co/storage/v1/object/public/generated-cvs/david-cv.pdf",
                documentsAttached = listOf("Driving Permit", "Recommendation Letter"),
                status = "Forwarded to Employer",
                appliedAt = "2026-07-20T16:00:00Z",
                jobTitle = "Regional Marketing Officer",
                jobOrganization = "Nile Breweries",
                candidateName = "David Mukasa",
                candidateEmail = "david.m@gmail.com",
                candidatePhone = "+256 701 443 221",
                adminNotes = "Screened and forwarded to HR Manager on July 20th."
            ),
            UserApplication(
                id = "app_104",
                userId = "user_004",
                jobId = "5",
                generatedCvUrl = "https://mock-supabase.co/storage/v1/object/public/generated-cvs/joan-cv.pdf",
                documentsAttached = listOf("CPA Certificate", "National ID"),
                status = "Accepted",
                appliedAt = "2026-07-19T14:20:00Z",
                jobTitle = "Senior Accountant",
                jobOrganization = "Stanbic Bank Uganda",
                candidateName = "Joan Katusiime",
                candidateEmail = "joan.kat@gmail.com",
                candidatePhone = "+256 755 889 001",
                adminNotes = "Candidate selected for 2nd round interviews."
            )
        )
    )
    val allApplications: StateFlow<List<UserApplication>> = _allApplications.asStateFlow()

    // Admin Reported Jobs Queue
    private val _reportedJobItems = MutableStateFlow<List<ReportedJobItem>>(
        listOf(
            ReportedJobItem(
                id = "rep_01",
                jobId = "8",
                jobTitle = "Agricultural Officer (Expired Demo)",
                organization = "Kasese District LG",
                reporterEmail = "applicant256@gmail.com",
                reason = "Application deadline has passed and portal link returns 404.",
                reportedAt = "2026-07-21T08:30:00Z"
            ),
            ReportedJobItem(
                id = "rep_02",
                jobId = "3",
                jobTitle = "Regional Marketing Officer",
                organization = "Nile Breweries",
                reporterEmail = "alex.k@gmail.com",
                reason = "Requirements mention Lusoga fluency, please clarify if Luganda is mandatory.",
                reportedAt = "2026-07-20T19:00:00Z"
            )
        )
    )
    val reportedJobItems: StateFlow<List<ReportedJobItem>> = _reportedJobItems.asStateFlow()

    fun addLocation(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_locations.value.contains(trimmed)) {
            _locations.value = _locations.value + trimmed
        }
    }

    fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_categories.value.contains(trimmed)) {
            _categories.value = _categories.value + trimmed
        }
    }

    fun addJobType(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_jobTypes.value.contains(trimmed)) {
            _jobTypes.value = _jobTypes.value + trimmed
        }
    }

    fun postJob(job: MockJob, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val newJobsList = _jobs.value.toMutableList()
            newJobsList.add(0, job)
            _jobs.value = newJobsList
            
            if (isRealSupabaseConnected) {
                client.insertJob(job)
            }
            _successMessage.value = "Job '${job.title}' posted successfully! Edge notification dispatched."
            _isLoading.value = false
            onComplete(true)
        }
    }

    fun updateJob(updatedJob: MockJob) {
        val list = _jobs.value.toMutableList()
        val idx = list.indexOfFirst { it.id == updatedJob.id }
        if (idx != -1) {
            list[idx] = updatedJob
            _jobs.value = list
            _successMessage.value = "Job '${updatedJob.title}' updated."
        }
    }

    fun updateApplicationStatus(appId: String, newStatus: String, notes: String? = null) {
        val list = _allApplications.value.toMutableList()
        val idx = list.indexOfFirst { it.id == appId }
        if (idx != -1) {
            val old = list[idx]
            list[idx] = old.copy(
                status = newStatus,
                adminNotes = notes ?: old.adminNotes
            )
            _allApplications.value = list
            _successMessage.value = "Application status updated to '$newStatus'."
        }
    }

    fun dismissReportedJob(reportId: String) {
        val list = _reportedJobItems.value.toMutableList()
        list.removeAll { it.id == reportId }
        _reportedJobItems.value = list
        _successMessage.value = "Report dismissed."
    }

    fun deactivateJob(jobId: String) {
        // Deactivate job status
        val jobList = _jobs.value.toMutableList()
        val idx = jobList.indexOfFirst { it.id == jobId }
        if (idx != -1) {
            val old = jobList[idx]
            jobList[idx] = old.copy(status = "archived")
            _jobs.value = jobList
        }
        // Also clear associated reports
        val reportList = _reportedJobItems.value.toMutableList()
        reportList.removeAll { it.jobId == jobId }
        _reportedJobItems.value = reportList
        _successMessage.value = "Job listing deactivated and removed from active search."
    }

    fun saveJobsToCache(jobList: List<MockJob>) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = jobList.toJsonString()
            prefs.cachedJobsJson = json
            val now = System.currentTimeMillis()
            prefs.cachedJobsTimestamp = now
            _lastCacheFormattedTime.value = formatRelativeTime(now)
        }
    }

    suspend fun loadJobsFromCache(): Boolean = withContext(Dispatchers.IO) {
        val cachedJson = prefs.cachedJobsJson
        val timestamp = prefs.cachedJobsTimestamp
        if (!cachedJson.isNullOrBlank()) {
            val list = parseMockJobList(cachedJson)
            if (list.isNotEmpty()) {
                _jobs.value = list
                _lastCacheFormattedTime.value = formatRelativeTime(timestamp)
                return@withContext true
            }
        }
        return@withContext false
    }

    fun saveProfileToCache(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.cachedProfileJson = profile.toJsonString()
            prefs.cachedProfileTimestamp = System.currentTimeMillis()
        }
    }

    fun loadProfileFromCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val cached = parseUserProfile(prefs.cachedProfileJson)
            if (cached != null) {
                _userProfile.value = cached
            }
        }
    }

    fun saveApplicationsToCache(applications: List<UserApplication>) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.cachedApplicationsJson = applications.toJsonString()
            prefs.cachedApplicationsTimestamp = System.currentTimeMillis()
        }
    }

    fun loadApplicationsFromCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val cached = parseUserApplicationList(prefs.cachedApplicationsJson)
            if (cached.isNotEmpty()) {
                _userApplications.value = cached
            }
        }
    }

    init {
        Log.d(TAG, "ViewModel Init. LoggedIn: ${prefs.isLoggedIn}, Connected: $isRealSupabaseConnected")
        setupNetworkMonitoring()
        viewModelScope.launch(Dispatchers.IO) {
            val hasCache = loadJobsFromCache()
            if (!hasCache) {
                saveJobsToCache(_jobs.value)
            }
            archiveExpiredJobs(silent = true)
            if (prefs.isLoggedIn) {
                loadUserProfileAndSubscription()
            }
        }
    }

    private fun setupNetworkMonitoring() {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            val activeNet = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(activeNet)
            val initialConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            _isOnline.value = initialConnected
            if (!initialConnected) {
                _offlineBannerState.value = OfflineBannerState.OFFLINE
                viewModelScope.launch(Dispatchers.IO) {
                    loadJobsFromCache()
                    loadProfileFromCache()
                    loadApplicationsFromCache()
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            try {
                cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        val wasOffline = !_isOnline.value
                        _isOnline.value = true
                        if (wasOffline) {
                            _offlineBannerState.value = OfflineBannerState.BACK_ONLINE
                            syncWithSupabaseOnReconnect()
                            viewModelScope.launch(Dispatchers.IO) {
                                kotlinx.coroutines.delay(2500)
                                _offlineBannerState.value = OfflineBannerState.HIDDEN
                            }
                        }
                    }

                    override fun onLost(network: Network) {
                        _isOnline.value = false
                        _offlineBannerState.value = OfflineBannerState.OFFLINE
                        viewModelScope.launch(Dispatchers.IO) {
                            loadJobsFromCache()
                            loadProfileFromCache()
                            loadApplicationsFromCache()
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register network callback", e)
            }
        }
    }

    fun syncWithSupabaseOnReconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            saveJobsToCache(_jobs.value)
            if (prefs.isLoggedIn) {
                loadUserProfileAndSubscription()
            }
        }
    }

    fun setTheme(theme: String) {
        prefs.themePreference = theme
        _themeMode.value = theme
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    /**
     * Load Profile and Subscription for logged-in user
     */
    private fun loadUserProfileAndSubscription() {
        val uid = prefs.userId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            
            if (!_isOnline.value) {
                loadProfileFromCache()
                loadApplicationsFromCache()
                _isLoading.value = false
                return@launch
            }

            // 1. Fetch Profile
            when (val profileRes = client.fetchProfile(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    var profile = profileRes.data
                    if (profile.referralCode.isNullOrBlank()) {
                        val genCode = client.ensureReferralCode(profile.id, profile.fullName)
                        profile = profile.copy(referralCode = genCode)
                        client.updateProfile(profile)
                    }
                    _userProfile.value = profile
                    saveProfileToCache(profile)
                    prefs.userRole = profile.role
                    prefs.userName = profile.fullName
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Profile Fetch Error: ${profileRes.message}")
                    val fallbackCode = client.ensureReferralCode(uid, prefs.userName ?: "User")
                    val fallbackProfile = UserProfile(
                        id = uid,
                        fullName = prefs.userName ?: "User",
                        phone = null,
                        role = prefs.userRole,
                        referralCode = fallbackCode
                    )
                    _userProfile.value = fallbackProfile
                    saveProfileToCache(fallbackProfile)
                }
            }

            // 2. Fetch Subscription
            when (val subRes = client.fetchSubscription(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userSubscription.value = subRes.data
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Subscription Fetch Error: ${subRes.message}")
                    // Create fallback local subscription
                    _userSubscription.value = UserSubscription(
                        id = "sub_local",
                        userId = uid,
                        planTier = "trial",
                        status = "trial",
                        trialEndsAt = "2026-08-04T12:00:00Z",
                        renewalDate = "2026-08-04T12:00:00Z"
                    )
                }
            }
            
            loadUserDocuments()
            loadUserApplications()
            loadUserReferrals()
            _isLoading.value = false
            checkAndRequestNotificationPermission(getApplication())
        }
    }

    /**
     * Fetch referral status records for active user
     */
    fun loadUserReferrals() {
        val uid = prefs.userId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            when (val res = client.fetchReferrals(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userReferrals.value = res.data
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Fetch referrals error: ${res.message}")
                }
            }
        }
    }

    /**
     * Handle incoming referral code from deep link or user input
     */
    fun handleReferralCodeReceived(code: String) {
        val clean = code.trim().uppercase()
        if (clean.isNotBlank()) {
            prefs.pendingReferralCode = clean
            _pendingReferralCode.value = clean
            _successMessage.value = "Referral code $clean applied! Complete signup to receive 7 days of Premium free."
        }
    }

    /**
     * Increment job view count and check if Other Services popup should trigger
     */
    fun onJobDetailViewed() {
        _jobViewsInSession.value += 1
        val profile = _userProfile.value
        val isHiddenPermanently = profile?.hideServicesPopup == true
        val isDismissedTemporarily = prefs.servicesPopupDismissedUntil > System.currentTimeMillis()

        if (_jobViewsInSession.value >= 3 && !isHiddenPermanently && !isDismissedTemporarily) {
            _shouldShowServicesPopup.value = true
        }
    }

    /**
     * Dismiss Other Services popup for a few days
     */
    fun dismissServicesPopupLater() {
        _shouldShowServicesPopup.value = false
        prefs.servicesPopupDismissedUntil = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L // 3 days
    }

    /**
     * Dismiss Other Services popup permanently by updating profile hide_services_popup
     */
    fun dismissServicesPopupPermanently() {
        _shouldShowServicesPopup.value = false
        val uid = prefs.userId ?: return
        viewModelScope.launch {
            val current = _userProfile.value
            if (current != null) {
                _userProfile.value = current.copy(hideServicesPopup = true)
            }
            client.updateHideServicesPopup(uid, true)
        }
    }

    /**
     * Mark app onboarding as completed
     */
    fun completeOnboarding() {
        prefs.hasCompletedOnboarding = true
        _hasCompletedOnboarding.value = true
    }

    /**
     * Check profile completeness and complete referral reward if user was referred
     */
    fun completeReferralRewardIfEligible(userId: String) {
        viewModelScope.launch {
            when (val res = client.completeReferralReward(userId)) {
                is SupabaseClient.ApiResult.Success -> {
                    if (res.data) {
                        _successMessage.value = "🎉 Referral Reward Activated! 7 extra days of Premium have been added to your account."
                        loadUserProfileAndSubscription()
                    }
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Complete referral reward error: ${res.message}")
                }
            }
        }
    }

    /**
     * Load user documents from vault
     */
    fun loadUserDocuments() {
        val uid = prefs.userId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            when (val res = client.fetchUserDocuments(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userDocuments.value = res.data
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Fetch user docs error: ${res.message}")
                }
            }
        }
    }

    /**
     * Load user applications
     */
    fun loadUserApplications() {
        val uid = prefs.userId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            if (!_isOnline.value) {
                loadApplicationsFromCache()
                return@launch
            }
            when (val res = client.fetchUserApplications(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    val sorted = res.data.sortedByDescending { it.appliedAt }
                    _userApplications.value = sorted
                    saveApplicationsToCache(sorted)
                }
                is SupabaseClient.ApiResult.Error -> {
                    Log.e(TAG, "Fetch user applications error: ${res.message}")
                    loadApplicationsFromCache()
                }
            }
        }
    }

    /**
     * Update User Profile details
     */
    fun updateUserProfile(profile: UserProfile, onComplete: (Boolean) -> Unit) {
        if (!_isOnline.value) {
            _errorMessage.value = "You're offline. Connect to the internet to update settings."
            onComplete(false)
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val res = client.updateProfile(profile)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userProfile.value = res.data
                    saveProfileToCache(res.data)
                    prefs.userName = res.data.fullName
                    _successMessage.value = "Profile updated successfully."
                    onComplete(true)
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = res.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Upload a document to storage and save its metadata row
     */
    fun uploadAndSaveDocument(
        documentType: String,
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (!_isOnline.value) {
            _errorMessage.value = "You're offline. Connect to the internet to upload documents to your vault."
            onComplete(false)
            return
        }
        val uid = prefs.userId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val path = "$uid/${UUID.randomUUID()}_$fileName"
            when (val uploadRes = client.uploadFile("user-documents", path, fileBytes, mimeType)) {
                is SupabaseClient.ApiResult.Success -> {
                    val fileUrl = uploadRes.data
                    when (val insertRes = client.insertUserDocument(uid, documentType, fileUrl)) {
                        is SupabaseClient.ApiResult.Success -> {
                            loadUserDocuments()
                            _successMessage.value = "Document successfully added to your vault."
                            onComplete(true)
                        }
                        is SupabaseClient.ApiResult.Error -> {
                            _errorMessage.value = "Failed to register document metadata: ${insertRes.message}"
                            onComplete(false)
                        }
                    }
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = "File upload failed: ${uploadRes.message}"
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Delete a document from the vault
     */
    fun deleteUserDocument(docId: String, fileUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val res = client.deleteUserDocument(docId, fileUrl)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userDocuments.value = _userDocuments.value.filter { it.id != docId }
                    _successMessage.value = "Document deleted successfully."
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = res.message
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Change Password
     */
    fun changePassword(newPassword: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val res = client.changePassword(newPassword)) {
                is SupabaseClient.ApiResult.Success -> {
                    _successMessage.value = res.data
                    onComplete(true)
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = res.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Permanent Account Deletion
     */
    fun deleteAccount(onComplete: (Boolean) -> Unit) {
        val uid = prefs.userId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val res = client.deleteAccount(uid)) {
                is SupabaseClient.ApiResult.Success -> {
                    logout()
                    _successMessage.value = "Your account and all associated documents/applications have been deleted permanently."
                    onComplete(true)
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = res.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Email / Password Login Action
     */
    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = client.login(email, password)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true
                    _userProfile.value = result.profile
                    loadUserProfileAndSubscription()
                    _successMessage.value = "Successfully logged in as ${result.profile.fullName}!"
                    onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> {
                    _errorMessage.value = result.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Google Sign In Action
     */
    fun loginWithGoogle(
        email: String = "patrickromanug@gmail.com",
        name: String = "Patrick Roman",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = client.loginWithGoogle(email, name)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true
                    _userProfile.value = result.profile
                    loadUserProfileAndSubscription()
                    _successMessage.value = "Successfully signed in as ${result.user.email}!"
                    onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> {
                    _errorMessage.value = result.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Email / Password Signup Action
     */
    fun signUp(email: String, password: String, fullName: String, phone: String, referralCode: String? = null, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = client.signUp(email, password, fullName, phone)) {
                is SupabaseClient.AuthResult.Success -> {
                    _isLoggedIn.value = true
                    _userProfile.value = result.profile

                    val refCodeToUse = referralCode?.takeIf { it.isNotBlank() } ?: _pendingReferralCode.value
                    if (!refCodeToUse.isNullOrBlank()) {
                        client.recordPendingReferral(result.user.id, refCodeToUse)
                        prefs.pendingReferralCode = null
                        _pendingReferralCode.value = null
                    }

                    loadUserProfileAndSubscription()
                    _successMessage.value = "Welcome to LS Services, $fullName! Your 14-day free trial subscription is active."
                    onComplete(true)
                }
                is SupabaseClient.AuthResult.Error -> {
                    _errorMessage.value = result.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Forgot Password Action
     */
    fun forgotPassword(email: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = client.forgotPassword(email)) {
                is SupabaseClient.ApiResult.Success -> {
                    _successMessage.value = result.data
                    onComplete(true)
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = result.message
                    onComplete(false)
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Log Out Action
     */
    fun logout() {
        client.logout()
        _isLoggedIn.value = false
        _userProfile.value = null
        _userSubscription.value = null
        currentTab = "home"
        _successMessage.value = "Logged out successfully."
    }

    fun toggleBookmark(jobId: String) {
        val current = _bookmarks.value.toMutableSet()
        if (current.contains(jobId)) {
            current.remove(jobId)
        } else {
            current.add(jobId)
        }
        _bookmarks.value = current
        prefs.guestBookmarks = current
        
        // TODO: For logged-in users, synchronize with the Supabase bookmarks table
    }

    fun reportJob(jobId: String, reason: String) {
        if (!_isOnline.value) {
            _errorMessage.value = "You're offline. Connect to the internet to report job listings."
            return
        }
        val current = _reportedJobs.value.toMutableSet()
        current.add(jobId)
        _reportedJobs.value = current
        prefs.reportedJobs = current
        
        // TODO: Insert a row into the Supabase reported_jobs table
    }

    fun applyJob(jobId: String) {
        val current = _appliedJobs.value.toMutableSet()
        current.add(jobId)
        _appliedJobs.value = current
        prefs.appliedJobs = current
        
        // TODO: In a later prompt, this will navigate through ApplyFlow and post to the Supabase applications table
    }

    fun getApplicationsSubmittedThisMonth(): Int {
        val now = LocalDate.now()
        val yearMonthStr = String.format("%04d-%02d", now.year, now.monthValue)
        return _userApplications.value.count { app ->
            app.appliedAt.startsWith(yearMonthStr)
        }
    }

    fun submitApplication(
        jobId: String,
        jobTitle: String,
        jobOrganization: String,
        generatedCvBytes: ByteArray,
        documentsAttached: List<String>,
        onResult: (Boolean, String) -> Unit
    ) {
        val uid = prefs.userId ?: run {
            onResult(false, "User not logged in.")
            return
        }
        
        val subscription = _userSubscription.value
        val monthlyLimit = subscription?.appliesMonthlyLimit ?: 5
        val submittedThisMonth = getApplicationsSubmittedThisMonth()
        if (submittedThisMonth >= monthlyLimit) {
            onResult(false, "LIMIT_REACHED")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val fileName = "cv_${System.currentTimeMillis()}.pdf"
            val cvUploadRes = client.uploadFile(
                bucketName = "generated-cvs",
                path = "$uid/$fileName",
                fileBytes = generatedCvBytes,
                mimeType = "application/pdf"
            )

            val cvUrl = when (cvUploadRes) {
                is SupabaseClient.ApiResult.Success -> cvUploadRes.data
                is SupabaseClient.ApiResult.Error -> {
                    _isLoading.value = false
                    onResult(false, "CV Upload Failed: ${cvUploadRes.message}")
                    return@launch
                }
            }

            val insertRes = client.insertApplication(
                userId = uid,
                jobId = jobId,
                generatedCvUrl = cvUrl,
                documentsAttached = documentsAttached,
                jobTitle = jobTitle,
                jobOrganization = jobOrganization
            )

            when (insertRes) {
                is SupabaseClient.ApiResult.Success -> {
                    val currentList = _userApplications.value.toMutableList()
                    currentList.add(0, insertRes.data)
                    _userApplications.value = currentList
                    
                    val applied = _appliedJobs.value.toMutableSet()
                    applied.add(jobId)
                    _appliedJobs.value = applied
                    prefs.appliedJobs = applied

                    _successMessage.value = "Application for $jobTitle submitted successfully!"
                    _isLoading.value = false
                    onResult(true, "Success")
                }
                is SupabaseClient.ApiResult.Error -> {
                    _isLoading.value = false
                    onResult(false, "Database Insert Failed: ${insertRes.message}")
                }
            }
        }
    }

    fun incrementViewsCount(jobId: String) {
        val currentJobs = _jobs.value.map { job ->
            if (job.id == jobId) {
                job.copy(viewsCount = job.viewsCount + 1)
            } else {
                job
            }
        }
        _jobs.value = currentJobs
        
        // If Supabase is active, increment in Supabase
        if (isRealSupabaseConnected) {
            viewModelScope.launch {
                // TODO: Perform Supabase RPC or UPDATE to increment views_count
            }
        }
    }

    fun getSimilarJobs(targetJob: MockJob): List<MockJob> {
        val applied = _appliedJobs.value
        return _jobs.value
            .filter { it.id != targetJob.id && it.status == "active" && !applied.contains(it.id) }
            .map { candidate ->
                var score = 0
                if (candidate.category == targetJob.category) score += 3
                if (candidate.location.equals(targetJob.location, ignoreCase = true)) score += 2
                if (candidate.jobType.equals(targetJob.jobType, ignoreCase = true)) score += 1
                Pair(candidate, score)
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(4)
    }

    /**
     * Automatic scheduled auto-expiry of jobs.
     * Marks any active job where deadline has passed to 'archived'.
     */
    fun archiveExpiredJobs(silent: Boolean = true): Int {
        var count = 0
        val today = LocalDate.now()
        val referenceDate = if (today.isAfter(LocalDate.of(2026, 7, 1))) today else LocalDate.of(2026, 7, 21)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        
        val updatedJobs = _jobs.value.map { job ->
            try {
                val deadlineDate = LocalDate.parse(job.deadline, formatter)
                if (deadlineDate.isBefore(referenceDate) && job.status == "active") {
                    count++
                    job.copy(status = "archived")
                } else {
                    job
                }
            } catch (e: Exception) {
                job
            }
        }
        _jobs.value = updatedJobs
        if (!silent && count > 0) {
            _successMessage.value = "Automated Clean: $count expired listings archived successfully!"
        }
        return count
    }

    // ==========================================
    // NOTIFICATION SYSTEM PROPERTIES & FUNCTIONS
    // ==========================================

    fun isNotificationPermissionGranted(context: android.content.Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun checkAndRequestNotificationPermission(context: android.content.Context) {
        if (!prefs.isLoggedIn) return
        if (isNotificationPermissionGranted(context)) {
            registerPushToken()
        } else {
            if (!prefs.hasShownNotifPermissionExplanation) {
                showNotificationExplanationDialog = true
            }
        }
    }

    fun onUserAcceptedNotifExplanation(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        showNotificationExplanationDialog = false
        prefs.hasShownNotifPermissionExplanation = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            registerPushToken()
        }
    }

    fun onUserDeclinedNotifExplanation() {
        showNotificationExplanationDialog = false
        prefs.hasShownNotifPermissionExplanation = true
    }

    fun registerPushToken() {
        val uid = prefs.userId ?: return
        val token = prefs.pushToken ?: return
        viewModelScope.launch {
            Log.d(TAG, "Registering push token: $token for user: $uid")
            client.upsertPushToken(uid, token)
        }
    }

    fun handleNotificationTap(jobId: String) {
        viewModelScope.launch {
            val job = _jobs.value.find { it.id == jobId }
            if (job != null) {
                globalJobDetailToShow = job
                currentTab = "home"
            } else {
                Log.e(TAG, "Job with id $jobId not found in local list")
            }
        }
    }

    fun updateNotifyAllJobs(enabled: Boolean) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val updatedProfile = current.copy(notifyAllJobs = enabled)
            when (val res = client.updateProfile(updatedProfile)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userProfile.value = res.data
                    _successMessage.value = "Notification settings updated successfully."
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = "Failed to update notification settings: ${res.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun updateNotifyMatchingPreferences(enabled: Boolean) {
        val current = _userProfile.value ?: return
        val currentSub = _userSubscription.value
        val isFreeTier = currentSub?.planTier == "free" && currentSub.status != "trial"
        if (isFreeTier && enabled) {
            showUpgradePrompt = true
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val updatedProfile = current.copy(notifyMatchingPreferences = enabled)
            when (val res = client.updateProfile(updatedProfile)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userProfile.value = res.data
                    _successMessage.value = "Targeted notification settings updated successfully."
                }
                is SupabaseClient.ApiResult.Error -> {
                    _errorMessage.value = "Failed to update notification settings: ${res.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun mockUpgradeToTier(tier: String) {
        val current = _userSubscription.value ?: return
        val calculatedNotifLimit = when (tier) {
            "basic" -> 10
            "premium" -> 50
            else -> null // premium_pro / trial
        }
        val calculatedAppliesLimit = when (tier) {
            "basic" -> 3
            "premium" -> 15
            else -> null // premium_pro / trial
        }
        val calculatedCategoriesLimit = when (tier) {
            "basic" -> 2
            "premium" -> 5
            else -> null // premium_pro / trial
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            val updatedSub = current.copy(
                planTier = tier,
                status = "active",
                notifDailyLimit = calculatedNotifLimit,
                appliesMonthlyLimit = calculatedAppliesLimit,
                categoriesLimit = calculatedCategoriesLimit
            )
            when (val res = client.updateSubscription(updatedSub)) {
                is SupabaseClient.ApiResult.Success -> {
                    _userSubscription.value = res.data
                    _successMessage.value = "Successfully upgraded to ${tier.replace("_", " ").uppercase()} Plan!"
                }
                is SupabaseClient.ApiResult.Error -> {
                    _userSubscription.value = updatedSub
                    _successMessage.value = "Successfully upgraded to ${tier.replace("_", " ").uppercase()} Plan locally!"
                }
            }
            _isLoading.value = false
        }
    }
}

data class MockJob(
    val id: String,
    val title: String,
    val organization: String,
    val location: String,
    val jobType: String,
    val category: String,
    val salary: String = "Negotiable",
    val deadline: String = "2026-08-15",
    val purpose: String = "To contribute to organizational growth and deliver excellent services.",
    val requirements: String = "• Strong communication skills\n• Relevant background or degree\n• Driven, integrity-focused and detail-oriented",
    val otherDetails: String = "Competitive package with medical cover and local allowances.",
    val opensExternally: Boolean = false,
    val officialLink: String = "https://lsrecruitingservices.com",
    val applicationMethod: String = "auto_apply", // "auto_apply", "requires_personal_account", "email_only"
    val requiredDocuments: List<String> = emptyList(),
    val postedBy: String? = null,
    val viewsCount: Int = 10,
    val status: String = "active", // "active", "archived"
    val createdAt: String = "2026-07-21T00:00:00Z"
)

data class ReportedJobItem(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val organization: String,
    val reporterEmail: String,
    val reason: String,
    val reportedAt: String
)
