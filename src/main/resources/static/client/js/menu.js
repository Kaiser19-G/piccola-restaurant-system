// Products data
const products = [
    {
        id: 1,
        name: "Broaster",
        price: 20.00,
        category: "Broaster",
        icon: "🍗"
    },
    {
        id: 2,
        name: "Mostrito",
        price: 20.00,
        category: "Salchipapas",
        icon: "🌭"
    },
    {
        id: 3,
        name: "Hamburguesa Royal",
        price: 20.00,
        category: "Hamburguesas",
        icon: "🍔"
    },
    {
        id: 4,
        name: "Salchipapa Clásica",
        price: 20.00,
        category: "Salchipapas",
        icon: "🍟"
    },
    {
        id: 5,
        name: "Coca Cola",
        price: 5.00,
        category: "Bebidas",
        icon: "🥤"
    },
    {
        id: 6,
        name: "Hamburguesa Clásica",
        price: 15.00,
        category: "Hamburguesas",
        icon: "🍔"
    },
    {
        id: 7,
        name: "Pollo Broaster Familiar",
        price: 35.00,
        category: "Broaster",
        icon: "🍗"
    },
    {
        id: 8,
        name: "Salchipapa Especial",
        price: 25.00,
        category: "Salchipapas",
        icon: "🌭"
    }
];

// Cart array - CARGAR DESDE LOCALSTORAGE
let cart = loadCartFromStorage();
let filteredProducts = [...products];

// DOM Elements
const productsContainer = document.getElementById('productsContainer');
const cartCount = document.getElementById('cartCount');
const headerCartCount = document.getElementById('headerCartCount');
const cartItems = document.getElementById('cartItems');
const cartTotal = document.getElementById('cartTotal');
const totalAmount = document.getElementById('totalAmount');
const checkoutBtn = document.getElementById('checkoutBtn');
const searchInput = document.getElementById('searchInput');
const categoryFilter = document.getElementById('categoryFilter');
const minPrice = document.getElementById('minPrice');
const maxPrice = document.getElementById('maxPrice');
const loading = document.getElementById('loading');
const emptyState = document.getElementById('emptyState');

// FUNCIONES DE LOCALSTORAGE
function saveCartToStorage() {
    try {
        localStorage.setItem('piccola_cart', JSON.stringify(cart));
    } catch (error) {
        console.error('Error al guardar carrito:', error);
    }
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

function clearCartStorage() {
    try {
        localStorage.removeItem('piccola_cart');
    } catch (error) {
        console.error('Error al limpiar carrito:', error);
    }
}

// Initialize the app
document.addEventListener('DOMContentLoaded', function() {
    showLoading();
    setTimeout(() => {
        hideLoading();
        renderProducts();
        // CARGAR CARRITO AL INICIAR
        updateCartUI();
    }, 1000);

    // Add event listeners
    if (searchInput) searchInput.addEventListener('input', debounce(applyFilters, 300));
    if (categoryFilter) categoryFilter.addEventListener('change', applyFilters);
    if (minPrice) minPrice.addEventListener('input', debounce(applyFilters, 300));
    if (maxPrice) maxPrice.addEventListener('input', debounce(applyFilters, 300));
});

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Show loading
function showLoading() {
    if (loading) {
        loading.style.display = 'block';
        if (productsContainer) productsContainer.style.display = 'none';
        if (emptyState) emptyState.style.display = 'none';
    }
}

// Hide loading
function hideLoading() {
    if (loading) loading.style.display = 'none';
    if (productsContainer) productsContainer.style.display = 'flex';
}

// Render products
function renderProducts() {
    if (!productsContainer) return;
    
    if (filteredProducts.length === 0) {
        productsContainer.style.display = 'none';
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    productsContainer.style.display = 'flex';
    if (emptyState) emptyState.style.display = 'none';
    
    productsContainer.innerHTML = filteredProducts.map(product => `
        <div class="col-lg-3 col-md-6 col-sm-6 col-12">
            <div class="product-card">
                <div class="product-image">
                    <span style="position: relative; z-index: 2;">${product.icon}</span>
                </div>
                <div class="product-body">
                    <h5 class="product-title">${product.name}</h5>
                    <p class="product-category">${product.category}</p>
                    <div class="product-price">S/ ${product.price.toFixed(2)}</div>
                    <button class="btn-add-cart" onclick="addToCart(${product.id})">
                        <i class="bi bi-cart-plus"></i> Agregar al carrito
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Apply filters
function applyFilters() {
    if (!searchInput || !categoryFilter || !minPrice || !maxPrice) return;
    
    const searchTerm = searchInput.value.toLowerCase().trim();
    const selectedCategory = categoryFilter.value;
    const minPriceValue = parseFloat(minPrice.value) || 0;
    const maxPriceValue = parseFloat(maxPrice.value) || Infinity;

    filteredProducts = products.filter(product => {
        const matchesSearch = product.name.toLowerCase().includes(searchTerm);
        const matchesCategory = !selectedCategory || product.category === selectedCategory;
        const matchesPrice = product.price >= minPriceValue && product.price <= maxPriceValue;
        
        return matchesSearch && matchesCategory && matchesPrice;
    });

    showLoading();
    setTimeout(() => {
        hideLoading();
        renderProducts();
    }, 300);
}

// Add to cart - GUARDAR EN LOCALSTORAGE
function addToCart(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) return;
    
    const existingItem = cart.find(item => item.id === productId);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            ...product,
            quantity: 1
        });
    }

    saveCartToStorage(); // GUARDAR CAMBIOS
    updateCartUI();
    showAddToCartAnimation();
}

// Show add to cart animation
function showAddToCartAnimation() {
    // Animar botón del body
    const cartBtn = document.querySelector('.cart-btn');
    if (cartBtn) {
        cartBtn.style.transform = 'scale(1.1)';
        cartBtn.style.background = 'var(--accent-color)';
        cartBtn.style.color = 'var(--dark-color)';
        
        setTimeout(() => {
            cartBtn.style.transform = 'scale(1)';
            cartBtn.style.background = 'var(--secondary-color)';
            cartBtn.style.color = 'white';
        }, 200);
    }

    // Animar botón del header
    const headerCartBtn = document.getElementById('cartBtn');
    if (headerCartBtn) {
        headerCartBtn.style.transform = 'scale(1.15) translateY(-2px)';
        
        setTimeout(() => {
            headerCartBtn.style.transform = 'translateY(-2px)';
        }, 200);
    }
}

// Remove from cart - GUARDAR EN LOCALSTORAGE
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    saveCartToStorage(); // GUARDAR CAMBIOS
    updateCartUI();
}

// Update quantity - GUARDAR EN LOCALSTORAGE
function updateQuantity(productId, change) {
    const item = cart.find(item => item.id === productId);
    if (item) {
        item.quantity += change;
        if (item.quantity <= 0) {
            removeFromCart(productId);
        } else {
            saveCartToStorage(); // GUARDAR CAMBIOS
            updateCartUI();
        }
    }
}

// Update cart UI - ACTUALIZADO PARA SINCRONIZAR AMBOS CONTADORES
function updateCartUI() {
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    // Actualizar contador del botón del body
    if (cartCount) {
        cartCount.textContent = totalItems;
    }

    // Actualizar contador del header
    if (headerCartCount) {
        headerCartCount.textContent = totalItems;
        headerCartCount.style.display = totalItems > 0 ? 'flex' : 'none';
    }

    // Actualizar modal del carrito
    if (cartItems) {
        if (cart.length === 0) {
            cartItems.innerHTML = `
                <div class="text-center text-muted">
                    <i class="bi bi-cart-x" style="font-size: 3rem;"></i>
                    <p class="mt-2">Tu carrito está vacío</p>
                </div>
            `;
            if (cartTotal) cartTotal.style.display = 'none';
            if (checkoutBtn) checkoutBtn.style.display = 'none';
        } else {
            cartItems.innerHTML = cart.map(item => `
                <div class="cart-item">
                    <div>
                        <h6 class="mb-1">${item.icon} ${item.name}</h6>
                        <small class="text-muted">${item.category}</small>
                        <div class="mt-2">
                            <button class="btn btn-sm btn-outline-secondary" onclick="updateQuantity(${item.id}, -1)">
                                <i class="bi bi-dash"></i>
                            </button>
                            <span class="mx-2 fw-bold">${item.quantity}</span>
                            <button class="btn btn-sm btn-outline-secondary" onclick="updateQuantity(${item.id}, 1)">
                                <i class="bi bi-plus"></i>
                            </button>
                        </div>
                    </div>
                    <div class="text-end">
                        <div class="fw-bold text-primary">S/ ${(item.price * item.quantity).toFixed(2)}</div>
                        <button class="btn btn-sm btn-outline-danger mt-1" onclick="removeFromCart(${item.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            `).join('');
            
            if (cartTotal) cartTotal.style.display = 'block';
            if (totalAmount) totalAmount.textContent = `S/ ${totalPrice.toFixed(2)}`;
            if (checkoutBtn) checkoutBtn.style.display = 'inline-block';
        }
    }
}

// Checkout function - LIMPIAR LOCALSTORAGE
function checkout() {
    if (cart.length === 0) return;
    
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const itemsList = cart.map(item => `${item.name} x${item.quantity}`).join(', ');
    
    alert(`¡Gracias por tu pedido!\n\nProductos: ${itemsList}\nTotal: S/ ${total.toFixed(2)}\n\n¡Nos pondremos en contacto contigo pronto!`);
    
    // Clear cart
    cart = [];
    clearCartStorage(); // LIMPIAR STORAGE
    updateCartUI();
    
    // Close modal
    const modal = document.getElementById('cartModal');
    if (modal && typeof bootstrap !== 'undefined') {
        const modalInstance = bootstrap.Modal.getInstance(modal);
        if (modalInstance) modalInstance.hide();
    }
}

// Asignar función de checkout al botón
if (checkoutBtn) {
    checkoutBtn.addEventListener('click', checkout);
}

// Función global para exponer al HTML
window.addToCart = addToCart;
window.removeFromCart = removeFromCart;
window.updateQuantity = updateQuantity;