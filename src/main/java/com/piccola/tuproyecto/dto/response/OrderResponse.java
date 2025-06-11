package com.piccola.tuproyecto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.piccola.tuproyecto.entity.enums.EstadoOrdenEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String numeroOrden;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private EstadoOrdenEnum estado;
    private BigDecimal total;
    private String direccionEntrega;
    private String telefonoContacto;
    private String metodoPago;
    private String notas;
    private List<OrderItemResponse> items;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String notas;
    }
}
