package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
    List<DetalleOrden> findByOrdenId(Long ordenId);
    
    @Query("SELECT do FROM DetalleOrden do WHERE do.producto.id = :productoId ORDER BY do.orden.fechaCreacion DESC")
    List<DetalleOrden> findByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT SUM(do.cantidad) FROM DetalleOrden do WHERE do.producto.id = :productoId AND " +
           "do.orden.fechaCreacion >= :fechaInicio AND do.orden.fechaCreacion <= :fechaFin")
    Long sumCantidadByProductoIdAndFechaRango(@Param("productoId") Long productoId, 
                                              @Param("fechaInicio") LocalDateTime fechaInicio, 
                                              @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT do.producto.id, do.producto.nombre, SUM(do.cantidad) as totalVendido " +
           "FROM DetalleOrden do " +
           "WHERE do.orden.estado = 'ENTREGADA' AND do.orden.fechaCreacion >= :fechaInicio AND do.orden.fechaCreacion <= :fechaFin " +
           "GROUP BY do.producto.id, do.producto.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT SUM(do.subtotal) FROM DetalleOrden do WHERE do.orden.estado = 'ENTREGADA' AND " +
           "do.orden.fechaCreacion >= :fechaInicio AND do.orden.fechaCreacion <= :fechaFin")
    java.math.BigDecimal sumIngresosByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                 @Param("fechaFin") LocalDateTime fechaFin);
}
