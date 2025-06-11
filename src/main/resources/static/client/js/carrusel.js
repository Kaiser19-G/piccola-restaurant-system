document.addEventListener('DOMContentLoaded', function() {
    // Variables
    const productosContainer = document.querySelector('.contenedor-productos-populares');
    const productos = document.querySelectorAll('.producto-item');
    const btnAtras = document.querySelector('.atras');
    const btnAdelante = document.querySelector('.adelante');
    const puntosContainer = document.querySelector('.puntos');
    
    let productoActual = 0;
    const totalProductos = productos.length;
    
    // Crear puntos indicadores con animación
    function crearPuntos() {
        puntosContainer.innerHTML = ''; // Limpiar contenedor de puntos
        
        for (let i = 0; i < totalProductos; i++) {
            const punto = document.createElement('div');
            punto.classList.add('punto');
            if (i === 0) {
                punto.classList.add('active');
            }
            punto.dataset.index = i;
            punto.addEventListener('click', () => {
                mostrarProducto(i);
            });
            puntosContainer.appendChild(punto);
        }
    }
    
    // Mostrar producto específico con animación mejorada
    function mostrarProducto(index) {
        if (index < 0) {
            productoActual = totalProductos - 1;
        } else if (index >= totalProductos) {
            productoActual = 0;
        } else {
            productoActual = index;
        }
        
        // Ocultar todos los productos primero
        productos.forEach((producto) => {
            producto.classList.remove('active');
        });
        
        // Pequeña pausa antes de mostrar el nuevo producto (para efecto visual)
        setTimeout(() => {
            // Actualizar la posición del carrusel
            productosContainer.style.transform = `translateX(-${productoActual * 100}%)`;
            
            // Mostrar el producto actual
            productos[productoActual].classList.add('active');
            
            // Actualizar puntos activos con animación
            const puntos = document.querySelectorAll('.punto');
            puntos.forEach((punto, i) => {
                punto.classList.remove('active');
                if (i === productoActual) {
                    // Pequeño retraso para la animación del punto
                    setTimeout(() => {
                        punto.classList.add('active');
                    }, 100);
                }
            });
        }, 100);
    }
    
    // Evento para el botón "atrás" con efectos
    btnAtras.addEventListener('click', () => {
        // Añadir efecto de pulsación al botón
        btnAtras.style.transform = 'translateY(-50%) scale(0.95)';
        setTimeout(() => {
            btnAtras.style.transform = 'translateY(-50%)';
        }, 100);
        
        mostrarProducto(productoActual - 1);
    });
    
    // Evento para el botón "adelante" con efectos
    btnAdelante.addEventListener('click', () => {
        // Añadir efecto de pulsación al botón
        btnAdelante.style.transform = 'translateY(-50%) scale(0.95)';
        setTimeout(() => {
            btnAdelante.style.transform = 'translateY(-50%)';
        }, 100);
        
        mostrarProducto(productoActual + 1);
    });
    
    // Inicializar carrusel
    crearPuntos();
    mostrarProducto(0);
    
    // Añadir función de autoplay con tiempo aleatorio para hacerlo más natural
    function iniciarAutoplay() {
        // Tiempo base de 4 segundos + aleatorio (hasta 2 segundos más)
        const tiempoAleatorio = 4000 + Math.random() * 2000;
        
        return setInterval(() => {
            mostrarProducto(productoActual + 1);
        }, tiempoAleatorio);
    }
    
    let intervalo = iniciarAutoplay();
    
    // Detener autoplay al interactuar con el carrusel
    productosContainer.addEventListener('mouseenter', () => {
        clearInterval(intervalo);
    });
    
    productosContainer.addEventListener('mouseleave', () => {
        intervalo = iniciarAutoplay();
    });
    
    // Agregar efecto de hover a los puntos
    puntosContainer.addEventListener('mouseover', (e) => {
        if (e.target.classList.contains('punto') && !e.target.classList.contains('active')) {
            e.target.style.transform = 'scale(1.1)';
        }
    });
    
    puntosContainer.addEventListener('mouseout', (e) => {
        if (e.target.classList.contains('punto') && !e.target.classList.contains('active')) {
            e.target.style.transform = 'scale(1)';
        }
    });
    
    // Para dispositivos táctiles: detectar deslizamiento con mejor manejo
    let touchStartX = 0;
    let touchEndX = 0;
    let isSwiping = false;
    
    productosContainer.addEventListener('touchstart', (e) => {
        touchStartX = e.touches[0].clientX;
        isSwiping = true;
        
        // Detener la transición durante el swipe para mayor responsividad
        productosContainer.style.transition = 'none';
    });
    
    productosContainer.addEventListener('touchmove', (e) => {
        if (!isSwiping) return;
        
        touchEndX = e.touches[0].clientX;
        const diffX = touchStartX - touchEndX;
        
        // Mostrar una vista previa del movimiento mientras se hace swipe
        const offset = -productoActual * 100 - (diffX / window.innerWidth * 25);
        productosContainer.style.transform = `translateX(${offset}%)`;
    });
    
    productosContainer.addEventListener('touchend', (e) => {
        touchEndX = e.changedTouches[0].clientX;
        productosContainer.style.transition = 'transform 0.6s cubic-bezier(0.22, 1, 0.36, 1)';
        isSwiping = false;
        handleSwipe();
    });
    
    function handleSwipe() {
        const swipeThreshold = 50;
        if (touchStartX - touchEndX > swipeThreshold) {
            // Deslizamiento a la izquierda - siguiente
            mostrarProducto(productoActual + 1);
        } else if (touchEndX - touchStartX > swipeThreshold) {
            // Deslizamiento a la derecha - anterior
            mostrarProducto(productoActual - 1);
        } else {
            // Si el swipe fue muy corto, volvemos a la posición actual
            productosContainer.style.transform = `translateX(-${productoActual * 100}%)`;
        }
    }
    
    // Teclas de flecha para navegación
    document.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') {
            mostrarProducto(productoActual - 1);
        } else if (e.key === 'ArrowRight') {
            mostrarProducto(productoActual + 1);
        }
    });
});