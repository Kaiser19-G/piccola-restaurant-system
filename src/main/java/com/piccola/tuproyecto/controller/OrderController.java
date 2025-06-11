package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.request.OrderRequest;
import com.piccola.tuproyecto.dto.response.OrderResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import com.piccola.tuproyecto.entity.enums.EstadoOrdenEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST para gestión de órdenes/pedidos
 * 
 * Endpoints para clientes autenticados:
 * - POST /api/orders - Crear nueva orden
 * - GET /api/orders - Historial de órdenes del usuario
 * - GET /api/orders/{id} - Detalle de orden
 * - PUT /api/orders/{id}/cancel - Cancelar orden
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    // TODO: Inyectar OrderService
    // private final OrderService orderService;

    /**
     * Crear nueva orden/pedido
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        // TODO: Implementar creación de orden
        // OrderResponse order = orderService.createOrder(request);
          return ResponseEntity.ok(OrderResponse.builder()
                .id(1L)
                .numeroOrden("ORD-001")
                .estado(EstadoOrdenEnum.PENDIENTE)
                .total(request.getTotal())
                .build());
    }

    /**
     * Obtener historial de órdenes del usuario actual
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(Pageable pageable) {
        // TODO: Implementar obtención de órdenes del usuario actual
        // String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Page<OrderResponse> orders = orderService.findOrdersByUser(username, pageable);
        
        return ResponseEntity.ok(Page.empty());
    }

    /**
     * Obtener detalle de una orden específica
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        // TODO: Implementar obtención de orden por ID (verificar que pertenece al usuario)
        // OrderResponse order = orderService.findOrderById(id);
          return ResponseEntity.ok(OrderResponse.builder()
                .id(id)
                .numeroOrden("ORD-" + String.format("%03d", id))
                .estado(EstadoOrdenEnum.PREPARANDO)
                .build());
    }    /**
     * Cancelar una orden (solo si está en estado PENDIENTE)
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long id) {
        // TODO: Implementar cancelación de orden
        // orderService.cancelOrder(id);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Orden cancelada exitosamente")
                .build());
    }

    /**
     * Obtener estado actual de una orden
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> getOrderStatus(@PathVariable Long id) {
        // TODO: Implementar consulta de estado
        // String status = orderService.getOrderStatus(id);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Estado de la orden")
                .data("EN_PREPARACION")
                .build());
    }

    /**
     * Calificar una orden completada
     */
    @PostMapping("/{id}/rating")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> rateOrder(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {
        
        // TODO: Implementar calificación de orden
        // orderService.rateOrder(id, rating, comment);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Calificación guardada exitosamente")
                .build());
    }
}
