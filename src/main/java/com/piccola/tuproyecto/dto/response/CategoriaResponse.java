package com.piccola.tuproyecto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para response de categorías
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Boolean activo;
    private Integer ordenVisualizacion;
    private Long cantidadProductos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
