package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByDisponibleTrueOrderByNombreAsc();
    
    List<Producto> findByCategoriaIdAndDisponibleTrueOrderByNombreAsc(Long categoriaId);
    
    List<Producto> findByDestacadoTrueAndDisponibleTrueOrderByNombreAsc();
    
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Producto> buscarProductosDisponibles(@Param("busqueda") String busqueda);
    
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND p.precio BETWEEN :precioMin AND :precioMax ORDER BY p.precio ASC")
    List<Producto> findByRangoPrecio(@Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax);
    
    @Query("SELECT p FROM Producto p JOIN p.recetas r JOIN r.ingrediente i WHERE i.id = :ingredienteId AND p.disponible = true")
    List<Producto> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);
    
    Optional<Producto> findByIdAndDisponibleTrue(Long id);
    
    Optional<Producto> findByIdAndActivoTrue(Long id);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT COUNT(do) FROM DetalleOrden do WHERE do.producto.id = :productoId")
    Long countVentasByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId ORDER BY p.nombre ASC")
    List<Producto> findByCategoriaIdOrderByNombreAsc(@Param("categoriaId") Long categoriaId);
    
    // Additional methods needed by ProductService
    List<Producto> findByActivoTrue();
    
    Page<Producto> findByActivoTrue(Pageable pageable);
    
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String searchTerm);
    
    List<Producto> findByPrecioBetweenAndActivoTrue(BigDecimal minPrice, BigDecimal maxPrice);
      List<Producto> findByDestacadoTrueAndActivoTrue();
    
    List<Producto> findByDisponibleTrueAndActivoTrue();
    
    Page<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId, Pageable pageable);
    
    boolean existsByNombreAndActivoTrue(String nombre);
}
