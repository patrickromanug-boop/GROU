
const allProducts = [
  // accessories
  { id: 1, name: "Italian Bussiness Butter Bag", price: 180000, category: "accessories", img: "images/accessories/Bags/IMG_4947.JPG", discount: "10% off", featured: true },
  { id: 2, name: "Bekia Clothing Leather Bag", price: 120000, category: "accessories", img:"images/accessories/Bags/IMG_4949.JPG", featured: true },
  { id: 3, name: "Cobolt Black Versace Shades2", price: 35000, category: "accessories", img: "images/accessories/shades/IMG_4938.JPG", newArrival: true },
  { id: 4, name: "Cobolt Black Versace Shades", price: 40000, category: "accessories", img: "images/accessories/shades/IMG_4939.JPG", discount: "15% off", featured: true },
  { id: 5, name: "Titanium Green Cedar Earrings set", price: 300000, category: "accessories", img: "images/accessories/women jewelry/IMG_4910.JPG", featured: true },
  { id: 7, name: "Diamond Ice Rocks & Earrings", price: 200000, category: "accessories", img: "images/accessories/women jewelry/IMG_4912.JPG", newArrival: true },
  { id: 8, name: "Berge Bag & Hat combo", price: 135000, category: "accessories", img: "images/accessories/capes/IMG_4978.JPG", newArrival: true },
  { id: 9, name: "Original Nike Golf Capes", price: 50000, category: "accessories", img: "images/accessories/capes/IMG_4973.JPG", newArrival: true },
  { id: 10, name: "Black Navy Cilock Watch", price: 450000, category: "accessories", img: "images/accessories/men jewelry/IMG_4916.JPG", newArrival: true },
  { id: 11, name: "Blue Carrera CR7 Watch", price: 300000, category: "accessories", img: "images/accessories/men jewelry/IMG_4917.JPG", newArrival: true },
  { id: 12, name: "Brown Mema Leather Watch", price: 1000000, category: "accessories", img: "images/accessories/men jewelry/IMG_4918.JPG", newArrival: true },
  { id: 13, name: "Sauvage Dior 100ml Perfume", price: 900000, category: "accessories", img: "images/accessories/perfumes/IMG_4988.JPG", newArrival: true },
  { id: 14, name: "Champaigane Calvin Kein Perfume", price: 1500000, category: "accessories", img: "images/accessories/perfumes/IMG_4989.JPG", newArrival: true },
  { id: 15, name: "Sterio Angel Unisex Shades", price: 100000, category: "accessories", img: "images/accessories/shades/IMG_4944.JPG", newArrival: true },
  // clothes
  { id: 16, name: "Embroidered Men Kanzus", price: 249000, category: "clothes", img: "images/clothes/men wear/IMG_5040.JPG", discount: "20% off", featured: true },
  { id: 17, name: "Modest Maxi CEO Kanzus", price: 189000, category: "clothes", img: "images/clothes/men wear/IMG_5042.JPG", featured: true },
  { id: 18, name: "African Chief Black Men cloth", price: 100000, category: "clothes", img: "images/clothes/men wear/IMG_5044.JPG", newArrival: true },
  { id: 19, name: "Black Haramain Kanzus", price: 200000, category: "clothes", img: "images/clothes/men wear/IMG_5045.JPG", discount: "Free shipping", featured: true },
  { id: 20, name: "Men's Casual Kanzus", price: 145000, category: "clothes", img: "images/clothes/men wear/IMG_5046.JPG", featured: true },
  { id: 21, name: "Elegant Flat Silk Kanzu", price: 99000, category: "clothes", img: "images/clothes/men wear/IMG_5048.JPG", newArrival: true },
  { id: 22, name: "Friday Muslim Wear kanzus", price: 70000, category: "clothes", img: "images/clothes/men wear/IMG_5049.JPG", newArrival: true },
  { id: 23, name: "Matching Iran wear Set", price: 300000, category: "clothes", img: "images/clothes/women wear/IMG_5050.JPG", newArrival: true },
  { id: 24, name: "Sheefon Calum Sharia", price: 150000, category: "clothes", img: "images/clothes/women wear/IMG_5051.JPG", newArrival: true },
  { id: 25, name: "Zari Satin Sharia", price: 500000, category: "clothes", img: "images/clothes/women wear/IMG_5052.JPG", newArrival: true },
  { id: 26, name: "Pure Muslim Sharia", price: 120000, category: "clothes", img: "images/clothes/women wear/IMG_5056.JPG", newArrival: true },
  { id: 27, name: "Colored Two Piece cotton ", price: 100000, category: "clothes", img: "images/clothes/women wear/IMG_5088.JPG", newArrival: true },
  { id: 28, name: "Wavy Colored Skirts", price: 50000, category: "clothes", img: "images/clothes/women wear/IMG_5058.JPG", newArrival: true },
  { id: 29, name: "Full Women Stripped Suits", price: 1500000, category: "clothes", img: "images/clothes/women wear/IMG_5067.JPG", newArrival: true },
  { id: 30, name: "Casual Office Blazers", price: 180000, category: "clothes", img: "images/clothes/women wear/IMG_5061.JPG", newArrival: true },
  //shoes
  { id: 31, name: "Sweet Love Red bottoms Heel", price: 1000000, category: "shoes", img: "images/shoes/office shoes/IMG_5039.JPG", discount: "5% off", featured: true },
  { id: 32, name: "Jiggle Bell Roofers Shoes", price: 119000, category: "shoes", img: "images/shoes/casual shoes/IMG_5006.JPG", featured: true },
  { id: 33, name: "Bridal Lord Lady Heel", price: 400000, category: "shoes", img: "images/shoes/office shoes/IMG_5037.JPG", newArrival: true },
  { id: 34, name: "Colored Unisex Roofers Shoes", price: 200000, category: "shoes", img: "images/shoes/casual shoes/IMG_5008.JPG", discount: "12% off", featured: true },
  { id: 35, name: "Cross Heel Soft Shoes", price: 900000, category: "shoes", img: "images/shoes/office shoes/IMG_5036.JPG", featured: true },
  { id: 36, name: "Oxford Hard Leather Shoes", price: 700000, category: "shoes", img: "images/shoes/office shoes/IMG_4998.JPG", newArrival: true },
  { id: 37, name: "Brown Ebony Leather Shoes", price: 150000, category: "shoes", img: "images/shoes/office shoes/IMG_5002.JPG", newArrival: true },
  { id: 38, name: "Chanel Soft White Heels", price: 150000, category: "shoes", img: "images/shoes/office shoes/IMG_5033.JPG", newArrival: true },
  { id: 39, name: "Masturd Brown Gentle Shoes", price: 500000, category: "shoes", img: "images/shoes/office shoes/IMG_5003.JPG", newArrival: true },
  { id: 40, name: "Mexician Boss Brown shoes", price: 180000, category: "shoes", img: "images/shoes/office shoes/IMG_5005.JPG", newArrival: true },
  { id: 41, name: "New Balance Sneakers", price: 100000, category: "shoes", img: "images/shoes/sneakers/IMG_5001.JPG", newArrival: true },
  { id: 42, name: "Green Volvet Heels", price: 190000, category: "shoes", img: "images/shoes/office shoes/IMG_5032.JPG", newArrival: true },
  { id: 43, name: "Naked Wolf Casual sneakers", price: 100000, category: "shoes", img: "images/shoes/sneakers/IMG_5011.JPG", newArrival: true },
  { id: 44, name: "Nike AirForce Sneakers", price: 120000, category: "shoes", img: "images/shoes/sneakers/IMG_5012.JPG", newArrival: true },
  { id: 45, name: "Easy Freash Boy style ", price: 70000, category: "shoes", img: "images/shoes/sneakers/IMG_5013.JPG", newArrival: true },
];

let currentUser = null;  
let cart = JSON.parse(localStorage.getItem('cart')) || [];
let currentCategory = null;
let currentSearch = "";
let currentPageNum = 1;
const itemsPerPage = 15;
let chatMessages = JSON.parse(localStorage.getItem('chatMessages')) || [{ sender: "AHMEDZHUB Support", text: "Hello! Need fashion advice? Ask us!", timestamp: Date.now() }];


const ADMIN_EMAIL = "admin@modastyle.com";
const ADMIN_PASS = "admin123";
const ADMIN_NAME = "Style Admin";

function saveCart() { localStorage.setItem('cart', JSON.stringify(cart)); updateCartUI(); }
function updateCartUI() { document.getElementById('cartCount').innerText = cart.reduce((s,i)=>s+(Number(i.qty) ||0),0); renderCartSidebar(); }

function renderCartSidebar() {
  let container = document.getElementById('cartItemsList');
  if(!container) return;
  if(cart.length===0){ container.innerHTML="<p>Your bag is empty ✨</p>"; document.getElementById('cartTotal').innerHTML="Total: UGX 0"; document.getElementById('upsellSuggestions').innerHTML=""; return; }
  let html=""; let total=0;
  cart.forEach(item=>{ total+=item.price*item.qty; html+=`<div class='cart-item'><div><b>${item.name}</b> x${item.qty}</div><div>UGX ${(item.price*item.qty).toLocaleString()}<button class='remove-item' data-id='${item.id}' style='margin-left:12px;background:#a0341f;padding:4px 10px;border-radius:30px;'>🗑️</button></div></div>`; });
  document.getElementById('cartTotal').innerHTML=`Total: UGX ${total.toLocaleString()}`;
  container.innerHTML=html;
  let cartIds = cart.map(i=>i.id);
  let upsell = allProducts.filter(p=>!cartIds.includes(p.id)).slice(0,2);
  let upsellHtml = upsell.map(p=>`<div style="display:flex; justify-content:space-between; margin:8px 0;"><span>${p.name}</span><button class="addUpsell" data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">+ Add</button></div>`).join('');
  document.getElementById('upsellSuggestions').innerHTML=upsellHtml;
  document.querySelectorAll('.remove-item').forEach(btn=>btn.addEventListener('click',(e)=>{ let id=parseInt(btn.dataset.id); cart=cart.filter(i=>i.id!==id); saveCart(); renderCartSidebar(); updateCartUI(); }));
  document.querySelectorAll('.addUpsell').forEach(btn=>btn.addEventListener('click',(e)=>{ let id=parseInt(btn.dataset.id), name=btn.dataset.name, price=parseInt(btn.dataset.price); let exist=cart.find(i=>i.id===id); if(exist) exist.qty+=1; else cart.push({id,name,price,qty:1}); saveCart(); renderCartSidebar(); updateCartUI(); }));
}

function generateReceipt(orderTotal, items){
  let receipt =
   `🏷️ MODASTYLE RECEIPT 🏷️/n━━━━━━━━━━━━━━━━━━━━━/nDate: ${new Date().toLocaleString()}/nCustomer: ${currentUser?.name || 'Guest'} (${currentUser?.email || 'guest'})/n━━━━━━━━━━━━━━━━━━━━━/nItems:/n${items.map(i=>`  ✦ ${i.name} x${i.qty} = UGX ${(i.price*i.qty).toLocaleString()}`).join('/n')}/n━━━━━━━━━━━━━━━━━━━━━/nTOTAL: UGX ${orderTotal.toLocaleString()}/n💳 Payment Successful!/nThank you for shopping at ModaStyle!`;
  alert(receipt);
  let orders = JSON.parse(localStorage.getItem('orders')) || [];
  orders.unshift({ id: Date.now(), user: currentUser?.name || 'Guest', email: currentUser?.email || 'guest', items: [...items], total: orderTotal, date: new Date().toISOString() });
  localStorage.setItem('orders', JSON.stringify(orders));
  if(currentUser?.isAdmin) refreshAdminDashboard();
}

document.getElementById('checkoutButton')?.addEventListener('click',()=>{
  if(cart.length===0){ alert("Add items to your bag first ❤️"); return; }
  if(!currentUser){ alert("Please login to complete purchase"); openAuthModal(); return; }
  let total = cart.reduce((s,i)=>s+i.price*i.qty,0);
  generateReceipt(total, cart);
  cart = [];
  saveCart();
  document.getElementById('cartSidebar').classList.remove('open');
});

function getFilteredProducts(){
  let filtered = [...allProducts];
  if(currentCategory && currentCategory !== 'all') filtered = filtered.filter(p=>p.category === currentCategory);
  if(currentSearch.trim() !== "") filtered = filtered.filter(p=>p.name.toLowerCase().includes(currentSearch.toLowerCase()));
  return filtered;
}

function renderProductListing() {
    let filtered = getFilteredProducts();
    let totalPages = Math.ceil(filtered.length / itemsPerPage);
    let start = (currentPageNum - 1) * itemsPerPage;
    let paginated = filtered.slice(start, start + itemsPerPage);
    
    let html = `<div class="section-title">${currentCategory ? currentCategory.toUpperCase() + " Collection" : "All Products"}</div>`;
    html += `<div class="product-grid">`;
    
    paginated.forEach(p => {
        html += `<div class="product-card">
            <div class="product-img">
                <img src="${p.img}" alt="${p.name}">
            </div>
            <div class="product-title">${p.name}</div>
            <div class="price">UGX ${p.price.toLocaleString()}</div>
            <button class="btn-add" data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">Add to Cart</button>
        </div>`;
    });
    
    html += `</div><div class="pagination">`;
    for(let i = 1; i <= totalPages; i++) {
        html += `<button class="page-btn" data-page="${i}">${i}</button>`;
    }
    html += `</div>`;
    
    document.getElementById('appContainer').innerHTML = html;
    
    attachAddEvents();
}
  document.querySelectorAll('.page-btn').forEach(btn=>btn.addEventListener('click',(e)=>{currentPageNum = parseInt(btn.dataset.page); renderProductListing(); }));


function attachAddEvents(){
  document.querySelectorAll('.btn-add').forEach(btn=>{
    btn.addEventListener('click',(e)=>{
      let id=parseInt(btn.dataset.id), name=btn.dataset.name, price=parseInt(btn.dataset.price);
      let exist=cart.find(i=>i.id===id);
      if(exist) exist.qty+=1;
      else cart.push({id,name,price,qty:1});
      saveCart();
      alert(`✨ ${name} added to your bag!`);
    });
  });
}

function renderHome(){
  let featured = allProducts.filter(p=>p.featured).slice(0,6);
  let newItems = allProducts.filter(p=>p.newArrival).slice(0,4);
    let html = 
    `<div class="home-container"style="background-image:url("images/accessories/Bags/IMG_4950.JPG");background-size:cover;
    background-position:
        center;
    background-attachment:
        fixed;
    padding:20px;  
    border-radius: 15px;">
    <div style="background:rgba(255,255,255,0,9); padding: 20px;
    border-radius: 15px;"> 
    </div>
    <div class="deal-banner"><h2>FLASH SALE: UP TO 25% OFF</h2><p>Use code: RATIBU25 | Free shipping on orders over UGX 1M</p></div>
    <div class="section-title">Editor's Pick (Featured)</div>
    <div class="product-grid">${featured.map(p=>`<div class="product-card"><div class="product-img"><img src="${p.img}" alt="${p.name}"></div><div class="product-title">${p.name}</div><div class="price">UGX ${p.price.toLocaleString()}</div><button class="btn-add" data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">Add to Cart</button></div>`).join('')}</div>
    <div class="section-title">Just Landed</div>
    <div class="product-grid">${newItems.map(p=>`<div class="product-card"><div class="product-img"><img src="${p.img}" alt="${p.name}"></div><div class="product-title">${p.name}</div><div class="price">UGX ${p.price.toLocaleString()}</div><button class="btn-add" data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">Add to Cart</button></div>`).join('')}</div>
    <div class="mv-grid"><div class="mv-card"><i class="fas fa-bullhorn fa-2x"></i><h3>Our Mission</h3><p>Bring affordable & trendy fashion to every Ugandan.</p></div><div class="mv-card"><i class="fas fa-globe-africa fa-2x"></i><h3>Vision</h3><p>Africa's leading modest & modern fashion hub.</p></div><div class="mv-card"><i class="fas fa-heart fa-2x"></i><h3>Goal</h3><p>Empower confidence through style & quality.</p></div></div>
    `;

document.getElementById('appContainer').innerHTML = html;
attachAddEvents();
}

function renderAbout(){
    const html=`<div class="mv-grid">
        <div class="mv-card">
            <h2>📖 Our Story</h2>
            <p>AHMEDZHUB began in  Namasuba with a passion for diverse fashion — from office elegance to modest luxury.</p>
        </div>
        <div class="mv-card">
            <h2>👥 Our Customers</h2><p>We serve women & men who love quality, modesty and trend.</p>
        </div>
        <div class="mv-card">
            <h2>🤝 The Team</h2>
            <p>Musaazi Ratibu (Founder), Jengwant Desmond (Design Curator), Mrs.Jannat (Customer Love)</p>
        </div>
    </div>`;
    document.getElementById('appContainer').innerHTML = html;
}
function renderContact(){ 
    const html=`<div class="mv-grid">
        <div class="mv-card">
            <h2>📞 Free Toll</h2>
            <p>Phone: 0200 804 020</p>
            <p>Email: care@ahmedstyle.ug</p>
            <p>📍 Kampala, Acacia Mall, Level 2</p>
            <iframe width="100%" height="220" style="border:0" loading="lazy" src="https://maps.google.com/maps?q=kampala&t=&z=13&ie=UTF8&iwloc=&output=embed"></iframe>
            <p>Chat with us live 7am-9pm</p>
        </div>
    </div>`;
    document.getElementById('appContainer').innerHTML = html;
}


function handleLogin(email, password, name){
  if(email === ADMIN_EMAIL && password === ADMIN_PASS){
    currentUser = { name: ADMIN_NAME, email: ADMIN_EMAIL, isAdmin: true };
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
    document.getElementById('userNameDisplay').innerHTML = `👑 Admin`;
    document.getElementById('adminDashboardLink').style.display = 'inline-block';
    alert(`Welcome Admin ${ADMIN_NAME}! Full access.`);
    document.getElementById('authModal').style.display = 'none';
    refreshAdminDashboard();
    document.getElementById('adminDashboardModal').style.display = 'flex';
    return true;
  } else if(email && password && name){
    currentUser = { name: name, email: email, isAdmin: false };
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
    document.getElementById('userNameDisplay').innerHTML = `👤 ${name.split(' ')[0]}`;
    document.getElementById('adminDashboardLink').style.display = 'none';
    alert(`Welcome ${name}, enjoy shopping!`);
    document.getElementById('authModal').style.display = 'none';
    return true;
  } else { alert("Please fill all fields"); return false; }
}

function openAuthModal(){ document.getElementById('authModal').style.display='flex'; }
function logout(){ currentUser=null; localStorage.removeItem('currentUser'); document.getElementById('userNameDisplay').innerHTML='Login'; document.getElementById('adminDashboardLink').style.display='none'; alert('Logged out'); renderHome(); }

function refreshAdminDashboard(){
  if(!currentUser?.isAdmin) return;
  let orders = JSON.parse(localStorage.getItem('orders')) || [];
  let totalRev = orders.reduce((s,o)=>s+o.total,0);
  document.getElementById('statOrders').innerText = orders.length;
  document.getElementById('statRevenue').innerHTML = totalRev.toLocaleString();
  let usersList = JSON.parse(localStorage.getItem('registeredUsers')) || [];
  document.getElementById('statUsers').innerText = usersList.length+1;
  let ordersHtml = orders.slice(0,8).map(o=>`<div style="border-bottom:1px solid #eee; padding:5px;"><b>#${o.id}</b> ${o.user} - UGX ${o.total.toLocaleString()} <small>${new Date(o.date).toLocaleDateString()}</small></div>`).join('');
  document.getElementById('adminOrdersList').innerHTML = ordersHtml || "<p>No orders yet</p>";
  let msgs = chatMessages.map(m=>`<div><b>${m.sender}:</b> ${m.text}</div>`).join('');
  document.getElementById('adminChatMessages').innerHTML = msgs;
  let productHtml = allProducts.map(p=>`<div>${p.name} (${p.category}) - UGX ${p.price.toLocaleString()}</div>`).join('');
  document.getElementById('adminProductList').innerHTML = productHtml;
}

function updateChatWidget(){ let box=document.getElementById('chatMessagesDisplay'); if(box) box.innerHTML=chatMessages.map(m=>`<div><b>${m.sender}:</b> ${m.text}</div>`).join(''); }


document.getElementById('submitAuthBtn')?.addEventListener('click',()=>{ let name=document.getElementById('authName').value, email=document.getElementById('authEmail').value, pass=document.getElementById('authPass').value; handleLogin(email,pass,name); });
document.getElementById('loginBtn')?.addEventListener('click',()=>{ if(currentUser) logout(); else openAuthModal(); });
document.getElementById('cartIconBtn')?.addEventListener('click',()=>document.getElementById('cartSidebar').classList.add('open'));
document.getElementById('closeCartBtn')?.addEventListener('click',()=>document.getElementById('cartSidebar').classList.remove('open'));
document.querySelectorAll('[data-category]').forEach(link=>{ link.addEventListener('click',(e)=>{ currentCategory=link.dataset.category; currentSearch=""; currentPageNum=1; renderProductListing(); }); });
document.querySelectorAll('[data-page]').forEach(link=>{ link.addEventListener('click',(e)=>{
    e.preventDefault();
    const page = link.dataset.page; 
    currentCategory=null; currentSearch=""; 
    if (page ==="home") renderHome();
    if (page ==="about") renderAbout();
    if (page ==="contact") renderContact(); }); });
document.getElementById('searchBtn')?.addEventListener('click',()=>{ currentSearch=document.getElementById('searchInput').value; currentCategory=null; currentPageNum=1; renderProductListing(); });
document.getElementById('adminDashboardLink')?.addEventListener('click',()=>{ if(currentUser?.isAdmin) { refreshAdminDashboard(); document.getElementById('adminDashboardModal').style.display='flex'; } else alert("Admin access only"); });
document.getElementById('closeAdminDashboardBtn')?.addEventListener('click',()=>document.getElementById('adminDashboardModal').style.display='none');
document.getElementById('sendAdminReplyBtn')?.addEventListener('click',()=>{ let reply=document.getElementById('adminReplyInput').value; if(reply && currentUser?.isAdmin){ chatMessages.push({sender:`Admin (${currentUser.name})`, text:reply}); localStorage.setItem('chatMessages',JSON.stringify(chatMessages)); refreshAdminDashboard(); updateChatWidget(); document.getElementById('adminReplyInput').value=''; } });
document.getElementById('sendChatMsgBtn')?.addEventListener('click',()=>{ let msg=document.getElementById('chatMsgInput').value; if(msg && currentUser){ chatMessages.push({sender:currentUser.name, text:msg}); localStorage.setItem('chatMessages',JSON.stringify(chatMessages)); updateChatWidget(); document.getElementById('chatMsgInput').value=''; if(currentUser.isAdmin) refreshAdminDashboard(); } else if(!currentUser) alert('Login to chat with support'); });
document.getElementById('openChatBtn')?.addEventListener('click',()=>{ document.getElementById('chatWidget').style.display='flex'; updateChatWidget(); });
document.querySelector('.chat-header')?.addEventListener('click',()=>document.getElementById('chatWidget').style.display='none');
document.getElementById('closeAuthModal')?.addEventListener('click',()=>document.getElementById('authModal').style.display='none');

function init(){ let stored=localStorage.getItem('currentUser'); if(stored){ currentUser=JSON.parse(stored); document.getElementById('userNameDisplay').innerHTML=currentUser.isAdmin?'👑 Admin':`👤 ${currentUser.name.split(' ')[0]}`; if(currentUser.isAdmin) document.getElementById('adminDashboardLink').style.display='inline-block'; } renderHome(); updateCartUI(); setInterval(()=>{ let d=new Date(); d.setDate(d.getDate()+2); if(document.getElementById('deliveryEstimate')) document.getElementById('deliveryEstimate').innerHTML=`🚚 Est. delivery: ${d.toDateString()}`; },100); }
init();

document.querySelectorAll('[data-category]').forEach(link => {
  link.addEventListener('click', (e) => {
    e.preventDefault();
    const category = e.target.dataset.category;  
    const filtered = allProducts.filter(p => p.category === category); 
    
    let html = `<div class="section-title">${category.toUpperCase()}</div>`;
    html += `<div class="product-grid">`;
    filtered.forEach(p => {
      html += `
        <div class="product-card">
          <div class="product-img">
            <img src="${p.img}" alt="${p.name}">
          </div>
          <div class="product-title">${p.name}</div>
          <div class="price">UGX ${p.price.toLocaleString()}</div>
          <button class="btn-add" data-id="${p.id}" data-name="${p.name}" data-price="${p.price}">Add to Cart</button>
        </div>`;
    });
    html += `</div>`;
    
    
    document.getElementById('appContainer').innerHTML = html;
    document.getElementById('home-page').style.display = 'none';
    document.getElementById('appContainer').style.display = 'block';
    
    
    document.querySelectorAll('.btn-add').forEach(btn => {
      btn.addEventListener('click', () => {
        let id = parseInt(btn.dataset.id);
        let name = btn.dataset.name;
        let price = parseInt(btn.dataset.price);
        addToCart(id, name, price);
      });
    });
  });
});