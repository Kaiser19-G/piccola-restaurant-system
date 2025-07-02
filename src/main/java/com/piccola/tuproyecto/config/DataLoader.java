package com.piccola.tuproyecto.config;

import com.piccola.tuproyecto.entity.*;
import com.piccola.tuproyecto.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

// @Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        log.info("Loading initial data...");        // Create admin user
        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .apellido("Sistema")
                .email("admin@piccola.pe")
                .password(passwordEncoder.encode("admin123"))
                .telefono("123456789")
                .rol(Rol.ADMIN)
                .build();
        usuarioRepository.save(admin);

        // Create employee user
        Usuario employee = Usuario.builder()
                .nombre("Empleado")
                .apellido("Cocina")
                .email("cocina@piccola.pe")
                .password(passwordEncoder.encode("cocina123"))
                .telefono("987654321")
                .rol(Rol.EMPLEADO)
                .build();
        usuarioRepository.save(employee);

        // Create test customer
        Usuario customer = Usuario.builder()
                .nombre("Cliente")
                .apellido("Prueba")
                .email("cliente@test.com")
                .password(passwordEncoder.encode("cliente123"))
                .telefono("999888777")
                .rol(Rol.CLIENTE)
                .build();
        usuarioRepository.save(customer);

        // Create categories
        Categoria hamburguesas = Categoria.builder()
                .nombre("Hamburguesas")
                .descripcion("Deliciosas hamburguesas artesanales")
                .activo(true)
                .build();
        categoriaRepository.save(hamburguesas);

        Categoria broaster = Categoria.builder()
                .nombre("Broaster")
                .descripcion("Pollo broaster crujiente")
                .activo(true)
                .build();
        categoriaRepository.save(broaster);

        Categoria salchipapas = Categoria.builder()
                .nombre("Salchipapas")
                .descripcion("Salchipapas especiales")
                .activo(true)
                .build();
        categoriaRepository.save(salchipapas);

        Categoria bebidas = Categoria.builder()
                .nombre("Bebidas")
                .descripcion("Refrescantes bebidas")
                .activo(true)
                .build();
        categoriaRepository.save(bebidas);

        // Create products
        createProducts(hamburguesas, broaster, salchipapas, bebidas);

        log.info("Initial data loaded successfully!");
        log.info("Admin login: admin@piccola.pe / admin123");
        log.info("Employee login: cocina@piccola.pe / cocina123");
        log.info("Customer login: cliente@test.com / cliente123");
    }

    private void createProducts(Categoria hamburguesas, Categoria broaster, Categoria salchipapas, Categoria bebidas) {
        // Hamburguesas
        Producto royaleBurger = Producto.builder()
                .nombre("Royale Burger")
                .descripcion("Hamburguesa con carne de res, queso, lechuga, tomate y salsa especial")
                .precio(new BigDecimal("15.50"))
                .categoria(hamburguesas)
                .imagen("/imagenes/royaleburguer.JPG")
                .disponible(true)
                .destacado(true)
                .activo(true)
                .build();
        productoRepository.save(royaleBurger);

        Producto mostrito = Producto.builder()
                .nombre("Mostrito Burger")
                .descripcion("Hamburguesa doble con carne, queso derretido y salsa mostaza")
                .precio(new BigDecimal("18.90"))
                .categoria(hamburguesas)
                .imagen("/imagenes/mostrito.JPG")
                .disponible(true)
                .destacado(true)
                .activo(true)
                .build();
        productoRepository.save(mostrito);

        // Broaster
        Producto broasterClassic = Producto.builder()
                .nombre("Broaster Clásico")
                .descripcion("Pollo broaster crujiente con papas fritas")
                .precio(new BigDecimal("12.90"))
                .categoria(broaster)
                .imagen("/imagenes/broaster.JPG")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(broasterClassic);

        Producto broasterFamiliar = Producto.builder()
                .nombre("Broaster Familiar")
                .descripcion("Pollo broaster para toda la familia con papas y ensalada")
                .precio(new BigDecimal("25.90"))
                .categoria(broaster)
                .imagen("/imagenes/broasterG.JPG")
                .disponible(true)
                .destacado(true)
                .activo(true)
                .build();
        productoRepository.save(broasterFamiliar);

        // Salchipapas
        Producto salchipapaClassica = Producto.builder()
                .nombre("Salchipapa Clásica")
                .descripcion("Papas fritas con salchichas y salsas")
                .precio(new BigDecimal("8.50"))
                .categoria(salchipapas)
                .imagen("/imagenes/salchipapa.JPG")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(salchipapaClassica);

        Producto salchipapaEspecial = Producto.builder()
                .nombre("Salchipapa Especial")
                .descripcion("Papas fritas con salchichas, huevo frito y queso")
                .precio(new BigDecimal("12.50"))
                .categoria(salchipapas)
                .imagen("/imagenes/salchipapa.JPG")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(salchipapaEspecial);

        // Bebidas
        Producto cocaCola = Producto.builder()
                .nombre("Coca Cola")
                .descripcion("Gaseosa Coca Cola 500ml")
                .precio(new BigDecimal("3.50"))
                .categoria(bebidas)
                .imagen("/imagenes/coca-cola.jpg")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(cocaCola);

        Producto incaKola = Producto.builder()
                .nombre("Inca Kola")
                .descripcion("Gaseosa Inca Kola 500ml")
                .precio(new BigDecimal("3.50"))
                .categoria(bebidas)
                .imagen("/imagenes/inca-kola.jpg")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(incaKola);

        Producto jugoNaranja = Producto.builder()
                .nombre("Jugo de Naranja")
                .descripcion("Jugo natural de naranja")
                .precio(new BigDecimal("4.50"))
                .categoria(bebidas)
                .imagen("/imagenes/jugo-naranja.jpg")
                .disponible(true)
                .destacado(false)
                .activo(true)
                .build();
        productoRepository.save(jugoNaranja);
    }
}
