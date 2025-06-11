package com.piccola.tuproyecto.service.impl;

import com.piccola.tuproyecto.dto.request.ProductRequest;
import com.piccola.tuproyecto.dto.response.ProductResponse;
import com.piccola.tuproyecto.entity.Categoria;
import com.piccola.tuproyecto.entity.Producto;
import com.piccola.tuproyecto.exception.ResourceNotFoundException;
import com.piccola.tuproyecto.repository.CategoriaRepository;
import com.piccola.tuproyecto.repository.ProductoRepository;
import com.piccola.tuproyecto.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsPaginated(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToProductResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        if (!categoriaRepository.existsByIdAndActivoTrue(categoryId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
        }
        return productoRepository.findByCategoriaIdAndActivoTrue(categoryId)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String searchTerm) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(searchTerm)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productoRepository.findByPrecioBetweenAndActivoTrue(minPrice, maxPrice)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts() {
        return productoRepository.findByDestacadoTrueAndActivoTrue()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .categoria(categoria)
                .imagen(request.getImagenUrl())
                .disponible(request.getDisponible() != null ? request.getDisponible() : true)
                .destacado(request.getDestacado() != null ? request.getDestacado() : false)
                .activo(true)
                .build();

        Producto savedProducto = productoRepository.save(producto);
        return mapToProductResponse(savedProducto);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (request.getCategoriaId() != null && !request.getCategoriaId().equals(producto.getCategoria().getId())) {
            Categoria categoria = categoriaRepository.findByIdAndActivoTrue(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }        if (request.getImagenUrl() != null) {
            producto.setImagen(request.getImagenUrl());
        }
        if (request.getDisponible() != null) {
            producto.setDisponible(request.getDisponible());
        }
        if (request.getDestacado() != null) {
            producto.setDestacado(request.getDestacado());
        }

        Producto updatedProducto = productoRepository.save(producto);
        return mapToProductResponse(updatedProducto);
    }

    @Override
    public void deleteProduct(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public void toggleProductAvailability(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        
        producto.setDisponible(!producto.getDisponible());
        productoRepository.save(producto);
    }    @Override
    public void toggleProductFeatured(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        
        producto.setDestacado(!producto.getDestacado());
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        return productoRepository.findByDisponibleTrueAndActivoTrue()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategoryPaginated(Long categoryId, Pageable pageable) {
        if (!categoriaRepository.existsByIdAndActivoTrue(categoryId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoryId);
        }
        return productoRepository.findByCategoriaIdAndActivoTrue(categoryId, pageable)
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsWithIngredient(Long ingredientId) {
        // For now, return empty list since ingredient relationship is not implemented yet
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getProductSalesCount(Long productId) {
        // This would require joining with order details table
        // For now, return 0 as a placeholder
        return 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return productoRepository.existsByNombreAndActivoTrue(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findProductEntityById(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    public void validateProductRequest(ProductRequest request) {
        // Validate that category exists
        if (request.getCategoriaId() != null && !categoriaRepository.existsByIdAndActivoTrue(request.getCategoriaId())) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + request.getCategoriaId());
        }
        
        // Validate that product name doesn't already exist (for create operations)
        if (request.getNombre() != null && existsByName(request.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + request.getNombre());
        }
    }

    private ProductResponse mapToProductResponse(Producto producto) {
        return ProductResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoriaId(producto.getCategoria().getId())
                .categoria(producto.getCategoria().getNombre())
                .categoriaNombre(producto.getCategoria().getNombre())                .imagenUrl(producto.getImagen())
                .disponible(producto.getDisponible())
                .destacado(producto.getDestacado())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaActualizacion(producto.getFechaActualizacion())
                .build();
    }
}
