package com.piccola.tuproyecto.controller.admin;

import com.piccola.tuproyecto.dto.request.ProductRequest;
import com.piccola.tuproyecto.dto.response.ProductResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

/**
 * Controlador de administración para gestión de productos del menú
 * 
 * Endpoints para administradores:
 * - GET /api/admin/products - Listar todos los productos
 * - POST /api/admin/products - Crear nuevo producto
 * - PUT /api/admin/products/{id} - Actualizar producto
 * - DELETE /api/admin/products/{id} - Eliminar producto
 * - POST /api/admin/products/{id}/image - Subir imagen
 */
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminProductController {

    // TODO: Inyectar ProductService
    // private final ProductService productService;

    /**
     * Listar todos los productos (incluye no disponibles)
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean available,
            Pageable pageable) {
        
        // TODO: Implementar listado completo de productos para admin
        // Page<ProductResponse> products = productService.findAllProducts(search, categoryId, available, pageable);
        
        return ResponseEntity.ok(Page.empty());
    }

    /**
     * Crear nuevo producto
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        // TODO: Implementar creación de producto
        // ProductResponse product = productService.createProduct(request);
        
        return ResponseEntity.ok(ProductResponse.builder()
                .id(1L)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .disponible(true)
                .build());
    }

    /**
     * Actualizar producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest request) {
        
        // TODO: Implementar actualización de producto
        // ProductResponse product = productService.updateProduct(id, request);
        
        return ResponseEntity.ok(ProductResponse.builder()
                .id(id)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .disponible(request.getDisponible())
                .build());
    }

    /**
     * Eliminar producto (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable Long id) {
        // TODO: Implementar eliminación lógica
        // productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Producto eliminado exitosamente")
                .build());
    }

    /**
     * Cambiar disponibilidad de producto
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Object>> toggleAvailability(@PathVariable Long id) {
        // TODO: Implementar toggle de disponibilidad
        // productService.toggleAvailability(id);
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Disponibilidad del producto actualizada")
                .build());
    }

    /**
     * Subir imagen del producto
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Object>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        // TODO: Implementar subida de imagen
        // String imageUrl = productService.uploadProductImage(id, file);
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Imagen subida exitosamente")
                .data("url-de-la-imagen")
                .build());
    }

    /**
     * Gestionar ingredientes de un producto
     */
    @PostMapping("/{id}/ingredients")
    public ResponseEntity<ApiResponse<Object>> updateProductIngredients(
            @PathVariable Long id,
            @RequestBody Long[] ingredientIds) {
        
        // TODO: Implementar gestión de ingredientes
        // productService.updateProductIngredients(id, ingredientIds);
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Ingredientes del producto actualizados")
                .build());
    }

    /**
     * Duplicar producto
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ProductResponse> duplicateProduct(@PathVariable Long id) {
        // TODO: Implementar duplicación de producto
        // ProductResponse newProduct = productService.duplicateProduct(id);
        
        return ResponseEntity.ok(ProductResponse.builder()
                .id(999L)
                .nombre("Copia de Producto")
                .build());
    }
}
