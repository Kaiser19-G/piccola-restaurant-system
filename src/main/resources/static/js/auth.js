// PICCOLA - JavaScript para autenticación

document.addEventListener('DOMContentLoaded', function() {
    initializeLoginForm();
    setupFormValidation();
});

function initializeLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
}

async function handleLogin(e) {
    e.preventDefault();
    
    const form = e.target;
    const submitBtn = form.querySelector('.btn-login');
    const btnText = submitBtn.querySelector('.btn-text');
    const spinner = submitBtn.querySelector('.loading-spinner');
    
    // Validación del formulario
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }
    
    // Mostrar loading
    btnText.style.display = 'none';
    spinner.style.display = 'inline-block';
    submitBtn.disabled = true;
    
    try {
        const formData = new FormData(form);
        const loginData = {
            email: formData.get('email'),
            password: formData.get('password'),
            rememberMe: formData.get('rememberMe') === 'on'
        };
        
        console.log('Enviando datos de login:', loginData);
        
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        console.log('Respuesta recibida:', response);
        const data = await response.json();
        console.log('Datos de respuesta:', data);
        
        if (response.ok && data.success) {
            // Guardar token y datos del usuario
            if (data.data && data.data.token) {
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('userRole', data.data.role);
                localStorage.setItem('userName', data.data.usuario);
                
                console.log('Usuario logueado:', {
                    role: data.data.role,
                    user: data.data.usuario
                });
            }
            
            // Mostrar éxito
            showAlert('success', '¡Login exitoso! Redirigiendo...');
            
            // Redireccionar según rol
            setTimeout(() => {
                const role = data.data.role;
                if (role === 'ADMIN') {
                    window.location.href = '/admin/dashboard';
                } else if (role === 'EMPLEADO') {
                    window.location.href = '/empleado/panel';
                } else {
                    window.location.href = '/';
                }
            }, 1500);
            
        } else {
            throw new Error(data.message || 'Error en el login');
        }
        
    } catch (error) {
        console.error('Error en login:', error);
        showAlert('danger', error.message || 'Error al iniciar sesión. Verifica tus credenciales.');
    } finally {
        // Ocultar loading
        btnText.style.display = 'inline-block';
        spinner.style.display = 'none';
        submitBtn.disabled = false;
    }
}

function setupFormValidation() {
    // Remover validación al escribir
    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('input', function() {
            if (this.checkValidity()) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        });
    });
}

function showAlert(type, message) {
    // Remover alertas existentes
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => alert.remove());
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const form = document.getElementById('loginForm');
    form.insertBefore(alertDiv, form.firstChild);
    
    // Auto-hide después de 5 segundos
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Funciones auxiliares para debugging
function debugLogin() {
    console.log('Estado del localStorage:');
    console.log('Token:', localStorage.getItem('token'));
    console.log('User Role:', localStorage.getItem('userRole'));
    console.log('User Name:', localStorage.getItem('userName'));
}

// Exponer función para debugging (solo desarrollo)
window.debugLogin = debugLogin;
