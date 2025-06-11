package com.piccola.tuproyecto.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para request de creación/actualización de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    // Disponibilidad del producto
    private Boolean disponible = true;

    // Tiempo estimado de preparación en minutos
    @Min(value = 1, message = "El tiempo de preparación debe ser mínimo 1 minuto")
    @Max(value = 120, message = "El tiempo de preparación no puede exceder 120 minutos")
    private Integer tiempoPreparacion;

    // Si es producto destacado
    private Boolean destacado = false;

    // Tags para búsqueda
    private List<String> tags;

    // Ingredientes del producto
    private List<Long> ingredienteIds;

    // Información nutricional opcional
    private Integer calorias;
    private String alergenos;

    // URL de la imagen (se puede cambiar por upload separado)
    private String imagenUrl;

    // Precio de costo (para reportes de rentabilidad)
    @DecimalMin(value = "0.0", message = "El precio de costo no puede ser negativo")
    private BigDecimal precioCosto;

    // Stock disponible (si manejan inventario)
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    // Producto activo/inactivo (para eliminar sin borrar)
    private Boolean activo = true;
}
