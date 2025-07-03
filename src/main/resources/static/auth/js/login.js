// PICCOLA LOGIN FUNCTIONALITY
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorAlert = document.getElementById('errorAlert');
    const successAlert = document.getElementById('successAlert');
    const loadingSpinner = document.getElementById('loadingSpinner');

    // Show/hide alerts
    function showAlert(type, message) {
        hideAllAlerts();
        if (type === 'error') {
            errorAlert.textContent = message;
            errorAlert.style.display = 'block';
        } else if (type === 'success') {
            successAlert.textContent = message;
            successAlert.style.display = 'block';
        }
    }

    function hideAllAlerts() {
        errorAlert.style.display = 'none';
        successAlert.style.display = 'none';
    }

    function showLoading(show) {
        loadingSpinner.style.display = show ? 'block' : 'none';
        const submitBtn = loginForm.querySelector('button[type="submit"]');
        submitBtn.disabled = show;
    }

    // Login form submission
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            hideAllAlerts();
            showLoading(true);

            const formData = new FormData(loginForm);
            const loginData = {
                email: formData.get('email'),
                password: formData.get('password')
            };

            try {
                console.log('Enviando datos de login:', loginData);

                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(loginData)
                });

                console.log('Respuesta del servidor:', response.status);

                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }

                const authData = await response.json();
                console.log('Datos de respuesta:', authData);

                // ✅ CORREGIDO: El backend devuelve directamente AuthResponse, no ApiResponse
                if (authData.token && authData.role) {
                    // Guardar datos de sesión
                    localStorage.setItem('jwt_token', authData.token);
                    localStorage.setItem('user_name', authData.nombre || authData.username || 'Usuario');
                    localStorage.setItem('user_email', authData.email);
                    localStorage.setItem('user_role', authData.role);

                    console.log('Login exitoso:', {
                        usuario: authData.nombre || authData.username,
                        email: authData.email,
                        rol: authData.role
                    });

                    showAlert('success', 'Login exitoso! Redirigiendo...');

                    // Obtener página de origen para redirección
                    const urlParams = new URLSearchParams(window.location.search);
                    const returnUrl = urlParams.get('returnUrl') || '/';

                    // Redireccionar según el rol o a la página de origen
                    setTimeout(() => {
                        if (authData.role === 'ADMIN') {
                            window.location.href = '/admin/dashboard';
                        } else if (authData.role === 'EMPLEADO') {
                            window.location.href = '/admin/orders';
                        } else {
                            // Cliente: redirigir a página de origen
                            window.location.href = returnUrl;
                        }
                    }, 1500);

                } else {
                    console.error('Datos incompletos en respuesta:', authData);
                    showAlert('error', 'Error de autenticación');
                }

            } catch (error) {
                console.error('Error de conexión:', error);
                if (error.message.includes('HTTP 401')) {
                    showAlert('error', 'Credenciales incorrectas');
                } else if (error.message.includes('HTTP 500')) {
                    showAlert('error', 'Error interno del servidor. Intenta más tarde.');
                } else {
                    showAlert('error', 'Error de conexión. Verifica tu internet.');
                }
            } finally {
                showLoading(false);
            }
        });
    }

    // Auto-hide alerts after 5 seconds
    function autoHideAlert(element) {
        if (element.style.display === 'block') {
            setTimeout(() => {
                element.style.display = 'none';
            }, 5000);
        }
    }

    // Observer para auto-ocultar alerts
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'attributes' && mutation.attributeName === 'style') {
                autoHideAlert(mutation.target);
            }
        });
    });

    observer.observe(errorAlert, { attributes: true });
    observer.observe(successAlert, { attributes: true });
});
