package com.piccola.tuproyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador para servir las páginas HTML de Thymeleaf
 * Maneja las rutas del frontend cliente para Hamburguesería PICCOLA
 */
@Controller
public class ViewController {
    
    // Páginas principales del cliente
    @GetMapping({"/", "/inicio", "/index"})
    public String index() {
        return "client/index";
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        // TODO: Cargar categorías y productos destacados
        return "client/menu";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "client/nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "client/contacto";
    }

    // Páginas del carrito y checkout
    @GetMapping("/carrito")
    public String carrito() {
        return "client/carrito";
    }

    @GetMapping("/checkout")
    public String checkout() {
        // TODO: Verificar autenticación del usuario
        return "client/checkout";
    }

    @GetMapping("/confirmacion/{orderId}")
    public String confirmacion(@PathVariable Long orderId, Model model) {
        // TODO: Cargar detalles de la orden
        model.addAttribute("orderId", orderId);        return "client/confirmacion_orden";
    }

    // Páginas del perfil de usuario
    @GetMapping("/perfil")
    public String perfil() {
        return "client/perfil/mis_datos";
    }

    @GetMapping("/perfil/ordenes")
    public String misOrdenes() {
        return "client/perfil/mis_ordenes";
    }

    // Detalle de producto
    @GetMapping("/producto/{id}")
    public String productoDetalle(@PathVariable Long id, Model model) {
        // TODO: Cargar producto por ID
        model.addAttribute("productId", id);
        return "client/producto_detalle";
    }

    // Páginas de administración
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/login_admin";
    }

    @GetMapping("/admin/kds")
    public String kitchenDisplay() {
        return "admin/kds";
    }

    @GetMapping("/admin/ordenes")
    public String gestionOrdenes() {
        return "admin/orders";
    }

    @GetMapping("/admin/productos")
    public String gestionProductos() {
        return "admin/products";
    }
}
