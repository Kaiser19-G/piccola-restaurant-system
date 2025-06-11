package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    
    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora AND " +
           "(p.usosMaximos IS NULL OR p.usosActuales < p.usosMaximos)")
    List<Promocion> findPromocionesActivas(@Param("ahora") LocalDateTime ahora);
    
    Optional<Promocion> findByCodigoPromocionAndActivoTrue(String codigoPromocion);
    
    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora AND " +
           "p.codigoPromocion = :codigo AND (p.usosMaximos IS NULL OR p.usosActuales < p.usosMaximos)")
    Optional<Promocion> findPromocionValidaByCodigo(@Param("codigo") String codigo, @Param("ahora") LocalDateTime ahora);
    
    List<Promocion> findByActivoTrueOrderByFechaCreacionDesc();
    
    @Query("SELECT p FROM Promocion p WHERE p.fechaFin < :ahora")
    List<Promocion> findPromocionesExpiradas(@Param("ahora") LocalDateTime ahora);
    
    @Query("SELECT p FROM Promocion p WHERE p.fechaInicio > :ahora AND p.activo = true")
    List<Promocion> findPromocionesFuturas(@Param("ahora") LocalDateTime ahora);
    
    boolean existsByCodigoPromocion(String codigoPromocion);
    
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.promocionAplicada.id = :promocionId")
    Long countUsosByPromocionId(@Param("promocionId") Long promocionId);
}
