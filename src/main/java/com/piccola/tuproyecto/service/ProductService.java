package com.piccola.tuproyecto.service;

import com.piccola.tuproyecto.dto.request.ProductRequest;
import com.piccola.tuproyecto.dto.response.ProductResponse;
import com.piccola.tuproyecto.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    
    List<ProductResponse> getAllProducts();
    
    List<ProductResponse> getAvailableProducts();
    
    List<ProductResponse> getProductsByCategory(Long categoryId);
    
    List<ProductResponse> getFeaturedProducts();
    
    ProductResponse getProductById(Long id);
    
    List<ProductResponse> searchProducts(String searchTerm);
    
    List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    ProductResponse createProduct(ProductRequest request);
    
    ProductResponse updateProduct(Long id, ProductRequest request);
    
    void deleteProduct(Long id);
    
    void toggleProductAvailability(Long id);
    
    void toggleProductFeatured(Long id);
    
    Page<ProductResponse> getProductsPaginated(Pageable pageable);
    
    Page<ProductResponse> getProductsByCategoryPaginated(Long categoryId, Pageable pageable);
    
    // Nuevos métodos para filtrado avanzado
    Page<ProductResponse> getProductsWithFilters(String search, Long categoryId, Boolean available, Pageable pageable);
    
    Page<ProductResponse> searchProductsPaginated(String searchTerm, Pageable pageable);
    
    Page<ProductResponse> getProductsByAvailability(Boolean available, Pageable pageable);
    
    List<ProductResponse> getProductsWithIngredient(Long ingredientId);
    
    Long getProductSalesCount(Long productId);
    
    boolean existsByName(String name);
    
    // Métodos internos para uso del servicio
    Producto findProductEntityById(Long id);
    
    void validateProductRequest(ProductRequest request);
}
