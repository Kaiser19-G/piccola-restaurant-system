package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.response.ProductResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de productos del menú
 * 
 * Endpoints públicos para el frontend cliente:
 * - GET /api/products - Listar productos con filtros
 * - GET /api/products/{id} - Detalle de producto
 * - GET /api/categories - Listar categorías
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    // TODO: Inyectar ProductService
    // private final ProductService productService;

    /**
     * Listar productos con filtros
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "true") boolean available,
            Pageable pageable) {
        
        // TODO: Implementar filtrado de productos
        // Page<ProductResponse> products = productService.findProducts(search, categoryId, minPrice, maxPrice, available, pageable);
        
        // Mock data por ahora
        return ResponseEntity.ok(Page.empty());
    }

    /**
     * Obtener detalle de un producto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        // TODO: Implementar obtención de producto por ID
        // ProductResponse product = productService.findById(id);
        
        return ResponseEntity.ok(ProductResponse.builder()
                .id(id)
                .nombre("Producto de ejemplo")
                .descripcion("Descripción del producto")
                .precio(BigDecimal.valueOf(15.90))
                .disponible(true)
                .build());
    }

    /**
     * Listar categorías disponibles
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        // TODO: Implementar obtención de categorías
        // List<String> categories = productService.findAllCategories();
        
        List<String> mockCategories = List.of(
                "Hamburguesas", 
                "Broaster", 
                "Salchipapas", 
                "Bebidas"
        );
        
        return ResponseEntity.ok(mockCategories);
    }

    /**
     * Productos destacados para la página de inicio
     */
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        // TODO: Implementar productos destacados
        // List<ProductResponse> featured = productService.findFeaturedProducts();
        
        return ResponseEntity.ok(List.of());
    }

    /**
     * Buscar productos por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String q) {
        // TODO: Implementar búsqueda de productos
        // List<ProductResponse> results = productService.searchByName(q);
        
        return ResponseEntity.ok(List.of());
    }    /**
     * Verificar disponibilidad de un producto
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(@PathVariable Long id) {
        // TODO: Verificar disponibilidad en tiempo real
        // boolean available = productService.isAvailable(id);
        
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("Producto disponible")
                .data(true)
                .build());
    }
}
