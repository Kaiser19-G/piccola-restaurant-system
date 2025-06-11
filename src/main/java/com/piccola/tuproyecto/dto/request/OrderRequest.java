package com.piccola.tuproyecto.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para request de creación de órdenes/pedidos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {    // Información del cliente (si no está autenticado)
    private String clienteNombre;
    private String clienteTelefono;
    private String clienteEmail;
    
    // Contact info for orders
    private String telefonoContacto;
    
    // Order notes
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;

    // Tipo de orden
    @NotBlank(message = "El tipo de orden es obligatorio")
    @Pattern(regexp = "DELIVERY|PICKUP", message = "Tipo de orden debe ser DELIVERY o PICKUP")
    private String tipoOrden;

    // Dirección para delivery
    private String direccionEntrega;
    private String referenciaEntrega;

    // Items de la orden
    @NotEmpty(message = "La orden debe tener al menos un item")
    @Valid
    private List<OrderItemRequest> items;

    // Totales
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El subtotal debe ser mayor a 0")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    private BigDecimal descuento = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El costo de delivery no puede ser negativo")
    private BigDecimal costoDelivery = BigDecimal.ZERO;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor a 0")
    private BigDecimal total;

    // Método de pago
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // EFECTIVO, TARJETA, YAPE, PLIN

    // Comentarios adicionales
    @Size(max = 500, message = "Los comentarios no pueden exceder 500 caracteres")
    private String comentarios;

    /**
     * DTO interno para items de la orden
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private BigDecimal precioUnitario;

        // Modificadores opcionales (ingredientes extra, sin cebolla, etc.)
        private List<String> modificadores;        // Comentarios específicos del item
        @Size(max = 200, message = "Los comentarios del item no pueden exceder 200 caracteres")
        private String comentarios;
        
        // Item notes
        @Size(max = 200, message = "Las notas del item no pueden exceder 200 caracteres")
        private String notas;
    }
}
