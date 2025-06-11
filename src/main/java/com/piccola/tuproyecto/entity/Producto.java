package com.piccola.tuproyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "imagen_url")
    private String imagen;
      @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean destacado = false;
    
    @Column(name = "tiempo_preparacion")
    private Integer tiempoPreparacion; // en minutos
    
    @Column(length = 50)
    private String tamaño; // pequeño, mediano, grande
    
    @Column(name = "calorias_aproximadas")
    private Integer caloriasAproximadas;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Receta> recetas;
    
    @ManyToMany(mappedBy = "productos")
    private List<Promocion> promociones;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrden> detallesOrden;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
