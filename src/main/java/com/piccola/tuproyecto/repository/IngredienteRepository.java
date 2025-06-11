package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    
    List<Ingrediente> findByDisponibleTrueOrderByNombreAsc();
    
    List<Ingrediente> findByTipoIngredienteAndDisponibleTrueOrderByNombreAsc(String tipoIngrediente);
    
    List<Ingrediente> findByEsAlergenoTrueAndDisponibleTrue();
    
    Optional<Ingrediente> findByNombreAndDisponibleTrue(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT i FROM Ingrediente i WHERE i.disponible = true AND " +
           "LOWER(i.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Ingrediente> buscarIngredientesDisponibles(@Param("busqueda") String busqueda);
    
    @Query("SELECT DISTINCT i.tipoIngrediente FROM Ingrediente i WHERE i.tipoIngrediente IS NOT NULL ORDER BY i.tipoIngrediente")
    List<String> findAllTiposIngrediente();
    
    @Query("SELECT i FROM Ingrediente i JOIN i.recetas r WHERE r.producto.id = :productoId")
    List<Ingrediente> findIngredientesByProductoId(@Param("productoId") Long productoId);
}
