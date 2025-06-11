package com.piccola.tuproyecto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para response de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;    private String categoria;
    private String categoriaNombre;
    private Long categoriaId;
    private Boolean disponible;
    private String imagenUrl;
    
    // Información adicional
    private Integer tiempoPreparacion;
    private Boolean destacado;
    private List<String> tags;
    private Integer calorias;
    private String alergenos;
    
    // Ingredientes del producto
    private List<IngredienteResponse> ingredientes;
    
    // Stock disponible
    private Integer stock;
    
    // Fechas de auditoría
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Rating promedio del producto
    private Double ratingPromedio;
    private Integer cantidadReviews;

    /**
     * DTO interno para ingredientes del producto
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredienteResponse {
        private Long id;
        private String nombre;
        private Boolean esAlergeno;
        private Boolean esOpcional;
    }
}
