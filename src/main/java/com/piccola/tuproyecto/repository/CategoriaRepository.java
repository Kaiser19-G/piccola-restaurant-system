package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    List<Categoria> findByActivoTrueOrderByOrdenVisualizacionAsc();
    
    Optional<Categoria> findByNombreAndActivoTrue(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT c FROM Categoria c WHERE c.activo = true AND c.productos IS NOT EMPTY ORDER BY c.ordenVisualizacion ASC")
    List<Categoria> findCategoriasConProductos();
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :categoriaId AND p.disponible = true")
    Long countProductosDisponiblesByCategoriaId(Long categoriaId);
    
    // Additional methods needed by ProductService
    boolean existsByIdAndActivoTrue(Long id);
    
    Optional<Categoria> findByIdAndActivoTrue(Long id);
}
