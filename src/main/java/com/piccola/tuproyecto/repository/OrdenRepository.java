package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Orden;
import com.piccola.tuproyecto.entity.enums.EstadoOrdenEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    Optional<Orden> findByNumeroOrden(String numeroOrden);

    Optional<Orden> findByIdAndActivoTrue(Long id);

    Optional<Orden> findByIdAndUsuarioIdAndActivoTrue(Long id, Long usuarioId);

    List<Orden> findByUsuarioIdAndActivoTrueOrderByFechaCreacionDesc(Long usuarioId);

    Page<Orden> findByActivoTrueOrderByFechaCreacionDesc(Pageable pageable);

    Page<Orden> findByEstadoAndActivoTrueOrderByFechaCreacionDesc(EstadoOrdenEnum estado, Pageable pageable);

    Page<Orden> findByFechaCreacionBetweenAndActivoTrueOrderByFechaCreacionDesc(LocalDateTime fechaInicio,
            LocalDateTime fechaFin, Pageable pageable);

    List<Orden> findByEstadoOrderByFechaCreacionAsc(EstadoOrdenEnum estado);

    List<Orden> findByEstadoInOrderByFechaCreacionAsc(List<EstadoOrdenEnum> estados);

    Long countByActivoTrue();

    Long countByEstadoAndActivoTrue(EstadoOrdenEnum estado);

    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin AND o.activo = true ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = :estado AND o.activo = true")
    BigDecimal sumTotalByEstadoAndActivoTrue(@Param("estado") EstadoOrdenEnum estado);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = 'ENTREGADO' AND o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin AND o.activo = true")
    BigDecimal calcularVentasByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado != 'CANCELADO' AND o.fechaCreacion >= :fechaInicio AND o.fechaCreacion <= :fechaFin AND o.activo = true")
    Long countOrdenesByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT o FROM Orden o LEFT JOIN o.usuario u WHERE o.activo = true AND u.email = :email ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesByEmail(@Param("email") String email);

    @Query("SELECT o FROM Orden o LEFT JOIN o.usuario u WHERE o.activo = true AND (" +
            "CAST(o.numeroOrden AS string) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(o.telefonoContacto) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Orden> buscarOrdenes(@Param("busqueda") String busqueda);

    @Query("SELECT AVG(o.total) FROM Orden o WHERE o.estado = 'ENTREGADO' AND o.activo = true")
    BigDecimal calcularTicketPromedio();
}
