// PICCOLA - Gestión del estado de autenticación en páginas del cliente

document.addEventListener('DOMContentLoaded', function() {
    initializeAuthState();
});

function initializeAuthState() {
    const userBtn = document.getElementById('userBtn');
    const userStatus = document.getElementById('userStatus');
    const userDropdown = document.getElementById('userDropdown');
    
    if (!userBtn) return;

    // Verificar si el usuario está logueado
    const token = localStorage.getItem('jwt_token');
    const userName = localStorage.getItem('user_name');
    const userEmail = localStorage.getItem('user_email');
    const userRole = localStorage.getItem('user_role');

    if (token && userName) {
        // Usuario logueado: mostrar dropdown
        setupLoggedInState(userBtn, userStatus, userDropdown, userName, userEmail, userRole);
    } else {
        // Usuario no logueado: botón de login normal
        setupLoggedOutState(userBtn, userStatus);
    }
}

function setupLoggedInState(userBtn, userStatus, userDropdown, userName, userEmail, userRole) {
    // Cambiar texto del botón
    if (userStatus) {
        userStatus.textContent = userName;
    }

    // Quitar el onclick de redirección a login
    userBtn.removeAttribute('onclick');
    
    // Agregar funcionalidad de dropdown
    userBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        toggleUserDropdown();
    });

    // Crear o actualizar el dropdown si no existe
    if (!userDropdown) {
        createUserDropdown(userBtn, userName, userEmail, userRole);
    } else {
        updateUserDropdown(userDropdown, userName, userEmail, userRole);
    }

    // Cerrar dropdown al hacer click fuera
    document.addEventListener('click', function(e) {
        if (!userBtn.contains(e.target)) {
            closeUserDropdown();
        }
    });
}

function setupLoggedOutState(userBtn, userStatus) {
    // Restaurar texto del botón
    if (userStatus) {
        userStatus.textContent = 'Iniciar Sesión';
    }
    
    // Asegurar que el onclick esté configurado
    const currentPage = window.location.pathname;
    userBtn.onclick = function() {
        window.location.href = '/login?returnUrl=' + encodeURIComponent(currentPage);
    };
}

function createUserDropdown(userBtn, userName, userEmail, userRole) {
    const dropdown = document.createElement('div');
    dropdown.id = 'userDropdown';
    dropdown.className = 'user-dropdown';
    dropdown.style.display = 'none';
    
    dropdown.innerHTML = `
        <div class="user-dropdown-header">
            <div class="user-info">
                <i class="fas fa-user-circle"></i>
                <div>
                    <div class="user-name">${userName}</div>
                    <div class="user-email">${userEmail}</div>
                </div>
            </div>
        </div>
        <div class="user-dropdown-body">
            <a href="/profile" class="dropdown-item">
                <i class="fas fa-user-edit"></i>
                Mi Perfil
            </a>
            <a href="/orders" class="dropdown-item">
                <i class="fas fa-shopping-bag"></i>
                Mis Pedidos
            </a>
            ${userRole === 'ADMIN' ? `
                <div class="dropdown-divider"></div>
                <a href="/admin/dashboard" class="dropdown-item">
                    <i class="fas fa-cog"></i>
                    Panel Admin
                </a>
            ` : ''}
            <div class="dropdown-divider"></div>
            <button class="dropdown-item logout-btn" onclick="logout()">
                <i class="fas fa-sign-out-alt"></i>
                Cerrar Sesión
            </button>
        </div>
    `;
    
    userBtn.parentNode.appendChild(dropdown);
}

function updateUserDropdown(dropdown, userName, userEmail, userRole) {
    // Actualizar información del usuario en el dropdown existente
    const userNameEl = dropdown.querySelector('.user-name');
    const userEmailEl = dropdown.querySelector('.user-email');
    
    if (userNameEl) userNameEl.textContent = userName;
    if (userEmailEl) userEmailEl.textContent = userEmail;
}

function toggleUserDropdown() {
    const dropdown = document.getElementById('userDropdown');
    if (dropdown) {
        const isVisible = dropdown.style.display === 'block';
        dropdown.style.display = isVisible ? 'none' : 'block';
    }
}

function closeUserDropdown() {
    const dropdown = document.getElementById('userDropdown');
    if (dropdown) {
        dropdown.style.display = 'none';
    }
}

function logout() {
    // Confirmar logout
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
        // Llamar al endpoint de logout si existe
        const token = localStorage.getItem('jwt_token');
        if (token) {
            fetch('/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                }
            }).catch(err => {
                console.warn('Error al notificar logout al servidor:', err);
            });
        }
        
        // Limpiar localStorage
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_name');
        localStorage.removeItem('user_email');
        localStorage.removeItem('user_role');
        
        // Recargar página para actualizar el estado
        window.location.reload();
    }
}

// Función para verificar si el token es válido (opcional)
function checkTokenValidity() {
    const token = localStorage.getItem('jwt_token');
    if (!token) return false;
    
    try {
        // Simple check - en una implementación real deberías verificar con el servidor
        const payload = JSON.parse(atob(token.split('.')[1]));
        const now = Date.now() / 1000;
        
        if (payload.exp < now) {
            // Token expirado
            localStorage.removeItem('jwt_token');
            localStorage.removeItem('user_name');
            localStorage.removeItem('user_email');
            localStorage.removeItem('user_role');
            return false;
        }
        
        return true;
    } catch (e) {
        return false;
    }
}

// Exponer funciones para uso global
window.logout = logout;
window.toggleUserDropdown = toggleUserDropdown;
