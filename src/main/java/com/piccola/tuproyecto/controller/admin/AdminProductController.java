package com.piccola.tuproyecto.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST API de administración para gestión de productos del menú
 * Este controlador maneja únicamente endpoints REST para APIs
 */
@RestController("adminProductApiController")
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminProductController {

    /**
     * Listar todos los productos (incluye no disponibles)
     */
    @GetMapping
    public ResponseEntity<String> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean available,
            Pageable pageable) {
        
        // TODO: Implementar listado completo de productos para admin
        return ResponseEntity.ok("API de productos funcionando correctamente");
    }
}
