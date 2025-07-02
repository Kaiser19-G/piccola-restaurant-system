package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.request.ProductRequest;
import com.piccola.tuproyecto.dto.response.ProductResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import com.piccola.tuproyecto.service.ProductService;
import com.piccola.tuproyecto.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador web de administración para gestión completa de productos
 * 
 * Funcionalidades:
 * - CRUD completo de productos (vistas web)
 * - Filtros por categoría, estado y búsqueda
 * - Paginación
 * - Eliminación lógica (soft delete)
 * - Manejo de imágenes
 * - Operaciones masivas
 * - Validación server-side
 */
@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminProductWebController {

    private final ProductService productService;
    private final CategoriaService categoriaService;

    /**
     * Mostrar página principal de gestión de productos
     */
    @GetMapping
    public String showProductsPage(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        
        try {
            log.info("Mostrando página de productos - search: {}, category: {}, status: {}", search, categoryId, status);
            
            // Obtener productos con filtros
            Page<ProductResponse> products = getFilteredProducts(search, categoryId, status, pageable);
            
            // Obtener categorías para el filtro
            List<?> categories = categoriaService.getAllActiveCategories();
            
            // Añadir atributos al modelo
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("currentSearch", search);
            model.addAttribute("currentCategory", categoryId);
            model.addAttribute("currentStatus", status);
            model.addAttribute("totalProducts", products.getTotalElements());
            
            return "admin/products";
            
        } catch (Exception e) {
            log.error("Error al cargar página de productos", e);
            model.addAttribute("error", "Error al cargar los productos");
            return "admin/products";
        }
    }

    /**
     * Mostrar formulario para crear nuevo producto
     */
    @GetMapping("/new")
    public String showNewProductForm(Model model) {
        try {
            log.info("Mostrando formulario de nuevo producto");
            
            model.addAttribute("product", new ProductRequest());
            model.addAttribute("categories", categoriaService.getAllActiveCategories());
            model.addAttribute("isEdit", false);
            
            return "admin/products-form";
            
        } catch (Exception e) {
            log.error("Error al mostrar formulario de nuevo producto", e);
            model.addAttribute("error", "Error al cargar el formulario");
            return "redirect:/admin/products";
        }
    }

    /**
     * Mostrar formulario para editar producto existente
     */
    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            log.info("Mostrando formulario de edición para producto ID: {}", id);
            
            ProductResponse product = productService.getProductById(id);
            
            // Convertir ProductResponse a ProductRequest para el formulario
            ProductRequest productRequest = ProductRequest.builder()
                    .nombre(product.getNombre())
                    .descripcion(product.getDescripcion())
                    .precio(product.getPrecio())
                    .categoriaId(product.getCategoriaId())
                    .imagenUrl(product.getImagenUrl())
                    .disponible(product.getDisponible())
                    .destacado(product.getDestacado())
                    .tiempoPreparacion(product.getTiempoPreparacion())
                    .calorias(product.getCalorias())
                    .alergenos(product.getAlergenos())
                    .build();
            
            model.addAttribute("product", productRequest);
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoriaService.getAllActiveCategories());
            model.addAttribute("isEdit", true);
            
            return "admin/products-form";
            
        } catch (Exception e) {
            log.error("Error al mostrar formulario de edición para producto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/admin/products";
        }
    }

    /**
     * Crear nuevo producto
     */
    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("product") ProductRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Creando nuevo producto: {}", request.getNombre());
            
            if (bindingResult.hasErrors()) {
                log.warn("Errores de validación al crear producto: {}", bindingResult.getAllErrors());
                model.addAttribute("categories", categoriaService.getAllActiveCategories());
                model.addAttribute("isEdit", false);
                return "admin/products-form";
            }
            
            // Validaciones adicionales
            if (productService.existsByName(request.getNombre())) {
                bindingResult.rejectValue("nombre", "error.product", "Ya existe un producto con este nombre");
                model.addAttribute("categories", categoriaService.getAllActiveCategories());
                model.addAttribute("isEdit", false);
                return "admin/products-form";
            }
            
            // Manejar subida de imagen si se proporciona
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = handleImageUpload(imageFile);
                request.setImagenUrl(imageUrl);
            }
            
            ProductResponse createdProduct = productService.createProduct(request);
            
            log.info("Producto creado exitosamente con ID: {}", createdProduct.getId());
            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");
            
            return "redirect:/admin/products";
            
        } catch (Exception e) {
            log.error("Error al crear producto", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el producto: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * Actualizar producto existente
     */
    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Actualizando producto ID: {} con datos: {}", id, request.getNombre());
            
            if (bindingResult.hasErrors()) {
                log.warn("Errores de validación al actualizar producto ID: {}", id);
                model.addAttribute("productId", id);
                model.addAttribute("categories", categoriaService.getAllActiveCategories());
                model.addAttribute("isEdit", true);
                return "admin/products-form";
            }
            
            // Manejar subida de imagen si se proporciona
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = handleImageUpload(imageFile);
                request.setImagenUrl(imageUrl);
            }
            
            ProductResponse updatedProduct = productService.updateProduct(id, request);
            
            log.info("Producto ID: {} actualizado exitosamente", id);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
            
            return "redirect:/admin/products";
            
        } catch (Exception e) {
            log.error("Error al actualizar producto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * Eliminar producto (soft delete)
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Eliminando producto ID: {}", id);
            
            productService.deleteProduct(id);
            
            log.info("Producto ID: {} eliminado exitosamente", id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al eliminar producto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Cambiar disponibilidad de producto
     */
    @PostMapping("/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Cambiando disponibilidad del producto ID: {}", id);
            
            productService.toggleProductAvailability(id);
            
            log.info("Disponibilidad del producto ID: {} cambiada exitosamente", id);
            redirectAttributes.addFlashAttribute("success", "Disponibilidad actualizada exitosamente");
            
        } catch (Exception e) {
            log.error("Error al cambiar disponibilidad del producto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la disponibilidad: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Cambiar estado destacado de producto
     */
    @PostMapping("/{id}/toggle-featured")
    public String toggleFeatured(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Cambiando estado destacado del producto ID: {}", id);
            
            productService.toggleProductFeatured(id);
            
            log.info("Estado destacado del producto ID: {} cambiado exitosamente", id);
            redirectAttributes.addFlashAttribute("success", "Estado destacado actualizado exitosamente");
            
        } catch (Exception e) {
            log.error("Error al cambiar estado destacado del producto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado destacado: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Eliminación masiva de productos
     */
    @PostMapping("/bulk-delete")
    public String bulkDeleteProducts(
            @RequestParam("productIds") List<Long> productIds,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Eliminación masiva de {} productos", productIds.size());
            
            for (Long productId : productIds) {
                productService.deleteProduct(productId);
            }
            
            log.info("Eliminación masiva completada para {} productos", productIds.size());
            redirectAttributes.addFlashAttribute("success", 
                String.format("%d productos eliminados exitosamente", productIds.size()));
            
        } catch (Exception e) {
            log.error("Error en eliminación masiva de productos", e);
            redirectAttributes.addFlashAttribute("error", "Error en la eliminación masiva: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Cambio masivo de disponibilidad
     */
    @PostMapping("/bulk-toggle-availability")
    public String bulkToggleAvailability(
            @RequestParam("productIds") List<Long> productIds,
            @RequestParam("available") boolean available,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Cambio masivo de disponibilidad para {} productos a: {}", productIds.size(), available);
            
            for (Long productId : productIds) {
                // Solo cambiar si el estado actual es diferente al deseado
                ProductResponse product = productService.getProductById(productId);
                if (product.getDisponible() != available) {
                    productService.toggleProductAvailability(productId);
                }
            }
            
            String action = available ? "habilitados" : "deshabilitados";
            log.info("Cambio masivo de disponibilidad completado para {} productos", productIds.size());
            redirectAttributes.addFlashAttribute("success", 
                String.format("%d productos %s exitosamente", productIds.size(), action));
            
        } catch (Exception e) {
            log.error("Error en cambio masivo de disponibilidad", e);
            redirectAttributes.addFlashAttribute("error", "Error en el cambio masivo: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtener productos con filtros aplicados
     */
    private Page<ProductResponse> getFilteredProducts(String search, Long categoryId, String status, Pageable pageable) {
        
        // Convertir status string a Boolean
        Boolean available = null;
        if (status != null && !status.trim().isEmpty()) {
            available = "disponible".equalsIgnoreCase(status);
        }
        
        // Usar el método de filtrado avanzado del servicio
        return productService.getProductsWithFilters(search, categoryId, available, pageable);
    }

    /**
     * Manejar subida de archivos de imagen
     */
    private String handleImageUpload(MultipartFile imageFile) {
        try {
            // Validar tipo de archivo
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Solo se permiten archivos JPG y PNG");
            }
            
            // Validar tamaño (máximo 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB en bytes
            if (imageFile.getSize() > maxSize) {
                throw new IllegalArgumentException("El archivo no puede ser mayor a 5MB");
            }
            
            // TODO: Implementar lógica real de subida de archivos
            // Por ahora, simulamos devolviendo un URL ficticio
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            String imageUrl = "/imagenes/productos/" + fileName;
            
            log.info("Imagen subida simulada: {}", imageUrl);
            
            return imageUrl;
            
        } catch (Exception e) {
            log.error("Error al subir imagen", e);
            throw new RuntimeException("Error al procesar la imagen: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS REST PARA AJAX ==========

    /**
     * API REST para obtener productos (usado por JavaScript)
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsApi(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        try {
            Page<ProductResponse> products = getFilteredProducts(search, categoryId, status, pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<ProductResponse>>builder()
                    .success(true)
                    .message("Productos obtenidos exitosamente")
                    .data(products)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error al obtener productos via API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<ProductResponse>>builder()
                            .success(false)
                            .message("Error al obtener productos: " + e.getMessage())
                            .build());
        }
    }

    /**
     * API REST para toggle de disponibilidad (usado por JavaScript)
     */
    @PostMapping("/{id}/api/toggle-availability")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> toggleAvailabilityApi(@PathVariable Long id) {
        try {
            productService.toggleProductAvailability(id);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Disponibilidad actualizada exitosamente")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error al cambiar disponibilidad del producto ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error al cambiar disponibilidad: " + e.getMessage())
                            .build());
        }
    }

    /**
     * API REST para toggle de destacado (usado por JavaScript)
     */
    @PostMapping("/{id}/api/toggle-featured")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> toggleFeaturedApi(@PathVariable Long id) {
        try {
            productService.toggleProductFeatured(id);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Estado destacado actualizado exitosamente")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error al cambiar estado destacado del producto ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error al cambiar estado destacado: " + e.getMessage())
                            .build());
        }
    }
}
