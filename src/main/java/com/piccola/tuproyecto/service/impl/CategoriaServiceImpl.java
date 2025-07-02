package com.piccola.tuproyecto.service.impl;

import com.piccola.tuproyecto.dto.response.CategoriaResponse;
import com.piccola.tuproyecto.entity.Categoria;
import com.piccola.tuproyecto.exception.ResourceNotFoundException;
import com.piccola.tuproyecto.repository.CategoriaRepository;
import com.piccola.tuproyecto.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAllActiveCategories() {
        return categoriaRepository.findByActivoTrueOrderByOrdenVisualizacionAsc()
                .stream()
                .map(this::mapToCategoriaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAllCategories() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::mapToCategoriaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse getCategoriaById(Long id) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return mapToCategoriaResponse(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getCategoriesWithProducts() {
        return categoriaRepository.findCategoriasConProductos()
                .stream()
                .map(this::mapToCategoriaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countProductsByCategory(Long categoryId) {
        return categoriaRepository.countProductosDisponiblesByCategoriaId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria findCategoryEntityById(Long id) {
        return categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    private CategoriaResponse mapToCategoriaResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .icono(categoria.getIcono())
                .activo(categoria.getActivo())
                .ordenVisualizacion(categoria.getOrdenVisualizacion())
                .cantidadProductos(countProductsByCategory(categoria.getId()))
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}
