package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.ForgotPasswordScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.SignUpScreen
import com.example.ui.tabs.*
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
    viewModel: AppViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
    // Screens: "login", "signup", "forgot_password", "main"
    var currentScreen by remember { mutableStateOf("main") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                "login" -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToSignUp = { currentScreen = "signup" },
                        onNavigateToForgotPassword = { currentScreen = "forgot_password" },
                        onNavigateToHome = { currentScreen = "main" }
                    )
                }
                "signup" -> {
                    SignUpScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentScreen = "login" },
                        onNavigateToHome = { currentScreen = "main" }
                    )
                }
                "forgot_password" -> {
                    ForgotPasswordScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentScreen = "login" }
                    )
                }
                "main" -> {
                    MainTabsShell(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentScreen = "login" }
                    )
                }
            }
        }
    }
}

@Composable
fun MainTabsShell(
    viewModel: AppViewModel,
    onNavigateToLogin: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val activeTab = viewModel.currentTab
    val activeApplyFlowJob = viewModel.activeApplyFlowJob

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.registerPushToken()
        }
    }

    val shouldShowServicesPopup by viewModel.shouldShowServicesPopup.collectAsState()

    if (shouldShowServicesPopup) {
        com.example.ui.components.OtherServicesPopup(viewModel = viewModel)
    }

    if (viewModel.showNotificationExplanationDialog) {
        com.example.ui.components.NotificationExplanationDialog(
            onConfirm = {
                viewModel.onUserAcceptedNotifExplanation(permissionLauncher)
            },
            onDismiss = {
                viewModel.onUserDeclinedNotifExplanation()
            }
        )
    }

    if (viewModel.showUpgradePrompt) {
        com.example.ui.components.UpgradeDialog(
            onDismiss = {
                viewModel.showUpgradePrompt = false
            },
            onUpgradeSuccess = {
                viewModel.showUpgradePrompt = false
                viewModel.currentTab = "profile"
                viewModel.profileSubScreen = "subscription_comparison"
            }
        )
    }

    if (activeApplyFlowJob != null) {
        ApplyFlowScreen(
            viewModel = viewModel,
            job = activeApplyFlowJob,
            onDismiss = { viewModel.activeApplyFlowJob = null }
        )
    } else {
        val tabs = remember {
            listOf(
                TabItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
                TabItem("saved", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
                TabItem("applications", "Applications", Icons.Filled.Assignment, Icons.Outlined.Assignment),
                TabItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
            )
        }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    tabs.forEach { tab ->
                        val isSelected = activeTab == tab.id
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.currentTab = tab.id },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = PrimaryBlue,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = PrimaryBlue
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.id}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                com.example.ui.components.OfflineBanner(viewModel = viewModel)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "TabTransition"
                    ) { tabId ->
                        when (tabId) {
                            "home" -> HomeScreen(viewModel, onNavigateToLogin)
                            "saved" -> SavedScreen(viewModel, onNavigateToHome = { viewModel.currentTab = "home" })
                            "applications" -> ApplicationsScreen(viewModel, onNavigateToLogin)
                            "profile" -> ProfileScreen(viewModel, onNavigateToLogin)
                            else -> HomeScreen(viewModel, onNavigateToLogin)
                        }
                    }
                }
            }
        }
    }
}

data class TabItem(
    val id: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
