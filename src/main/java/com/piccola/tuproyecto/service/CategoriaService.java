package com.piccola.tuproyecto.service;

import com.piccola.tuproyecto.dto.response.CategoriaResponse;
import com.piccola.tuproyecto.entity.Categoria;

import java.util.List;

public interface CategoriaService {
    
    List<CategoriaResponse> getAllActiveCategories();
    
    List<CategoriaResponse> getAllCategories();
    
    CategoriaResponse getCategoriaById(Long id);
    
    List<CategoriaResponse> getCategoriesWithProducts();
    
    Long countProductsByCategory(Long categoryId);
    
    // Métodos internos para uso del servicio
    Categoria findCategoryEntityById(Long id);
}
