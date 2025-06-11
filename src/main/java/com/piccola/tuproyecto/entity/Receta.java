package com.piccola.tuproyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;
    
    @Column(nullable = false)
    private Double cantidad;
    
    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida; // gramos, ml, unidades, etc.
    
    @Column(name = "es_opcional")
    private Boolean esOpcional = false;
    
    @Column(length = 500)
    private String instrucciones;
}
