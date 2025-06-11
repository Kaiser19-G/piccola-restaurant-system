// CART FUNCTIONALITY GLOBAL - Para usar en todas las páginas

// Cargar carrito desde localStorage
function loadGlobalCart() {
    try {
        const savedCart = localStorage.getItem('piccola_cart');
        return savedCart ? JSON.parse(savedCart) : [];
    } catch (error) {
        console.error('Error al cargar carrito:', error);
        return [];
    }
}

// Guardar carrito en localStorage
function saveGlobalCart(cart) {
    try {
        localStorage.setItem('piccola_cart', JSON.stringify(cart));
    } catch (error) {
        console.error('Error al guardar carrito:', error);
    }
}

// Actualizar contador del header en cualquier página
function updateGlobalCartCounter() {
    const cart = loadGlobalCart();
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    
    // Actualizar contador del header si existe
    const headerCartCount = document.getElementById('headerCartCount');
    if (headerCartCount) {
        headerCartCount.textContent = totalItems;
        headerCartCount.style.display = totalItems > 0 ? 'flex' : 'none';
    }
    
    // Actualizar contador del botón flotante si existe
    const cartCount = document.getElementById('cartCount');
    if (cartCount) {
        cartCount.textContent = totalItems;
    }
}

// Inicializar contador global cuando carga la página
document.addEventListener('DOMContentLoaded', function() {
    updateGlobalCartCounter();
});

// Actualizar cada 500ms para sincronizar entre pestañas
setInterval(updateGlobalCartCounter, 500);