package com.piccola.tuproyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(name = "codigo_promocion", unique = true, length = 50)
    private String codigoPromocion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento;
    
    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;
    
    @Column(name = "descuento_maximo", precision = 10, scale = 2)
    private BigDecimal descuentoMaximo;
    
    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;
    
    @Column(name = "usos_maximos")
    private Integer usosMaximos;
    
    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;
    
    @Column(name = "usos_por_cliente")
    private Integer usosPorCliente = 1;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "solo_primera_compra")
    private Boolean soloPrimeraCompra = false;
    
    @ManyToMany
    @JoinTable(
        name = "promocion_productos",
        joinColumns = @JoinColumn(name = "promocion_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;
    
    @OneToMany(mappedBy = "promocionAplicada", fetch = FetchType.LAZY)
    private List<Orden> ordenesAplicadas;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum TipoDescuento {
        PORCENTAJE, MONTO_FIJO, ENVIO_GRATIS, PRODUCTO_GRATIS
    }
}
