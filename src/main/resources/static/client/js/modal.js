  // MODAL FUNCTIONALITY - Actualizado para incluir carrito

// Variables globales
let userModal, cartModal, closeUser, closeCart, userBtn, cartBtn;
let loginForm, registerForm, showRegister, showLogin;

// Inicializar cuando se carga el DOM
document.addEventListener('DOMContentLoaded', function() {
    initializeModals();
    initializeCart();
});

function initializeModals() {
    // Elementos del DOM
    userModal = document.getElementById('userModal');
    cartModal = document.getElementById('cartModal');
    closeUser = document.getElementById('closeUser');
    closeCart = document.getElementById('closeCart');
    userBtn = document.getElementById('userBtn');
    cartBtn = document.getElementById('cartBtn');
    
    // Formularios de usuario
    loginForm = document.getElementById('loginForm');
    registerForm = document.getElementById('registerForm');
    showRegister = document.getElementById('showRegister');
    showLogin = document.getElementById('showLogin');

    // Event listeners para modales
    if (userBtn) userBtn.addEventListener('click', openUserModal);
    if (cartBtn) cartBtn.addEventListener('click', openCartModal);
    if (closeUser) closeUser.addEventListener('click', closeUserModal);
    if (closeCart) closeCart.addEventListener('click', closeCartModal);

    // Event listeners para formularios
    if (showRegister) showRegister.addEventListener('click', showRegisterForm);
    if (showLogin) showLogin.addEventListener('click', showLoginForm);
    if (loginForm) loginForm.addEventListener('submit', handleLogin);
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    // Cerrar modal al hacer clic fuera
    window.addEventListener('click', function(event) {
        if (event.target === userModal) closeUserModal();
        if (event.target === cartModal) closeCartModal();
    });
}

function initializeCart() {
    // Cargar carrito al inicializar
    updateCartModal();
    
    // Actualizar carrito cada segundo para sincronizar
    setInterval(updateCartModal, 1000);
}

// Funciones del modal de usuario
function openUserModal() {
    if (userModal) userModal.style.display = 'block';
}

function closeUserModal() {
    if (userModal) userModal.style.display = 'none';
}

function showRegisterForm() {
    if (loginForm) loginForm.style.display = 'none';
    if (registerForm) registerForm.style.display = 'block';
}

function showLoginForm() {
    if (registerForm) registerForm.style.display = 'none';
    if (loginForm) loginForm.style.display = 'block';
}

function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    // Aquí puedes agregar la lógica de autenticación
    alert(`¡Bienvenido de vuelta! Iniciando sesión para: ${email}`);
    closeUserModal();
}

function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        alert('Las contraseñas no coinciden');
        return;
    }
    
    // Aquí puedes agregar la lógica de registro
    alert(`¡Cuenta creada exitosamente! Bienvenido ${name}`);
    closeUserModal();
}

// FUNCIONES DEL CARRITO - NUEVAS
function openCartModal() {
    updateCartModal();
    if (cartModal) cartModal.style.display = 'block';
}

function closeCartModal() {
    if (cartModal) cartModal.style.display = 'none';
}

function loadCartFromStorage() {
    try {
        const savedCart = localStorage.getItem('piccola_cart');
        return savedCart ? JSON.parse(savedCart) : [];
    } catch (error) {
        console.error('Error al cargar carrito:', error);
        return [];
    }
}

function saveCartToStorage(cart) {
    try {
        localStorage.setItem('piccola_cart', JSON.stringify(cart));
    } catch (error) {
        console.error('Error al guardar carrito:', error);
    }
}

function updateCartModal() {
    const cart = loadCartFromStorage();
    const cartItems = document.getElementById('cartItems');
    const totalAmount = document.getElementById('totalAmount');
    const checkoutBtn = document.getElementById('checkoutBtn');
    
    if (!cartItems || !totalAmount) return;

    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    if (cart.length === 0) {
        cartItems.innerHTML = `
            <div class="cart-empty">
                <p>🛒 Tu carrito está vacío</p>
                <p>¡Agrega algunos productos deliciosos!</p>
            </div>
        `;
        if (checkoutBtn) checkoutBtn.style.display = 'none';
    } else {
        cartItems.innerHTML = cart.map(item => `
            <div class="cart-item">
                <div class="item-info">
                    <div class="item-name">${item.icon || '🍴'} ${item.name}</div>
                    <div class="item-price">S/ ${item.price.toFixed(2)} c/u</div>
                    <div class="item-quantity">
                        <button class="quantity-btn" onclick="updateCartQuantity(${item.id}, -1)">-</button>
                        <span>Cantidad: ${item.quantity}</span>
                        <button class="quantity-btn" onclick="updateCartQuantity(${item.id}, 1)">+</button>
                    </div>
                </div>
                <div class="item-actions">
                    <div style="color: yellow; font-weight: bold;">S/ ${(item.price * item.quantity).toFixed(2)}</div>
                    <button class="quantity-btn" onclick="removeFromCart(${item.id})" style="background-color: #d32f2f; margin-top: 5px;">🗑️</button>
                </div>
            </div>
        `).join('');
        
        if (checkoutBtn) checkoutBtn.style.display = 'inline-block';
    }
    
    totalAmount.textContent = totalPrice.toFixed(2);
}

// Funciones para manipular el carrito desde el modal
function updateCartQuantity(productId, change) {
    let cart = loadCartFromStorage();
    const item = cart.find(item => item.id === productId);
    
    if (item) {
        item.quantity += change;
        if (item.quantity <= 0) {
            cart = cart.filter(item => item.id !== productId);
        }
        saveCartToStorage(cart);
        updateCartModal();
    }
}

function removeFromCart(productId) {
    let cart = loadCartFromStorage();
    cart = cart.filter(item => item.id !== productId);
    saveCartToStorage(cart);
    updateCartModal();
}

function checkout() {
    const cart = loadCartFromStorage();
    if (cart.length === 0) return;
    
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const itemsList = cart.map(item => `${item.name} x${item.quantity}`).join(', ');
    
    alert(`¡Gracias por tu pedido!\n\nProductos: ${itemsList}\nTotal: S/ ${total.toFixed(2)}\n\n¡Nos pondremos en contacto contigo pronto!`);
    
    // Limpiar carrito
    localStorage.removeItem('piccola_cart');
    updateCartModal();
    closeCartModal();
}

// Hacer funciones globales para el HTML
window.updateCartQuantity = updateCartQuantity;
window.removeFromCart = removeFromCart;
window.checkout = checkout;