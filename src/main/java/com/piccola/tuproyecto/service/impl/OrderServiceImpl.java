package com.piccola.tuproyecto.service.impl;

import com.piccola.tuproyecto.dto.request.OrderRequest;
import com.piccola.tuproyecto.dto.response.OrderResponse;
import com.piccola.tuproyecto.entity.DetalleOrden;
import com.piccola.tuproyecto.entity.Orden;
import com.piccola.tuproyecto.entity.Producto;
import com.piccola.tuproyecto.entity.Usuario;
import com.piccola.tuproyecto.entity.enums.EstadoOrdenEnum;
import com.piccola.tuproyecto.repository.OrdenRepository;
import com.piccola.tuproyecto.repository.ProductoRepository;
import com.piccola.tuproyecto.repository.UsuarioRepository;
import com.piccola.tuproyecto.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        // Validar que todos los productos existan y estén disponibles
        List<DetalleOrden> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            Producto producto = productoRepository.findByIdAndActivoTrue(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getProductoId()));

            if (!producto.getDisponible()) {
                throw new RuntimeException("El producto '" + producto.getNombre() + "' no está disponible");
            }

            if (item.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetalleOrden detalle = DetalleOrden.builder()
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(subtotal)
                    .notas(item.getNotas())
                    .build();

            detalles.add(detalle);
        }

        // Crear la orden
        Orden orden = Orden.builder()
                .numeroOrden(generateOrderNumber())
                .usuario(usuario)
                .estado(EstadoOrdenEnum.PENDIENTE)
                .total(total)
                .direccionEntrega(request.getDireccionEntrega())
                .telefonoContacto(request.getTelefonoContacto())
                .metodoPago(request.getMetodoPago())
                .notas(request.getNotas())
                .activo(true)
                .build();

        // Asignar la orden a los detalles
        detalles.forEach(detalle -> detalle.setOrden(orden));
        orden.setDetalles(detalles);

        Orden savedOrden = ordenRepository.save(orden);
        return mapToOrderResponse(savedOrden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        return ordenRepository.findByUsuarioIdAndActivoTrueOrderByFechaCreacionDesc(usuario.getId())
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        Orden orden = ordenRepository.findByIdAndUsuarioIdAndActivoTrue(orderId, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        return mapToOrderResponse(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return ordenRepository.findByActivoTrueOrderByFechaCreacionDesc(pageable)
                .map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(EstadoOrdenEnum estado, Pageable pageable) {
        return ordenRepository.findByEstadoAndActivoTrueOrderByFechaCreacionDesc(estado, pageable)
                .map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByDateRange(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return ordenRepository.findByFechaCreacionBetweenAndActivoTrueOrderByFechaCreacionDesc(fechaInicio, fechaFin, pageable)
                .map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdAdmin(Long orderId) {
        Orden orden = ordenRepository.findByIdAndActivoTrue(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        return mapToOrderResponse(orden);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, EstadoOrdenEnum nuevoEstado) {
        Orden orden = ordenRepository.findByIdAndActivoTrue(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        // Validar transiciones de estado válidas
        if (!isValidStatusTransition(orden.getEstado(), nuevoEstado)) {
            throw new RuntimeException("Transición de estado no válida de " + orden.getEstado() + " a " + nuevoEstado);
        }

        orden.setEstado(nuevoEstado);

        // Si se completa la orden, establecer fecha de entrega
        if (nuevoEstado == EstadoOrdenEnum.ENTREGADO) {
            orden.setFechaEntrega(LocalDateTime.now());
        }

        Orden updatedOrden = ordenRepository.save(orden);
        return mapToOrderResponse(updatedOrden);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        Orden orden = ordenRepository.findByIdAndUsuarioIdAndActivoTrue(orderId, usuario.getId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        if (orden.getEstado() == EstadoOrdenEnum.ENTREGADO) {
            throw new RuntimeException("No se puede cancelar una orden que ya fue entregada");
        }

        if (orden.getEstado() == EstadoOrdenEnum.CANCELADO) {
            throw new RuntimeException("La orden ya está cancelada");
        }

        orden.setEstado(EstadoOrdenEnum.CANCELADO);
        Orden savedOrden = ordenRepository.save(orden);
        return mapToOrderResponse(savedOrden);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Orden orden = ordenRepository.findByIdAndActivoTrue(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        orden.setActivo(false);
        ordenRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSales() {
        BigDecimal total = ordenRepository.sumTotalByEstadoAndActivoTrue(EstadoOrdenEnum.ENTREGADO);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalOrdersCount() {
        return ordenRepository.countByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrdersCountByStatus(EstadoOrdenEnum estado) {
        return ordenRepository.countByEstadoAndActivoTrue(estado);
    }

    // Implementing missing methods from interface
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return getOrderByIdAdmin(id);
    }    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders() {
        List<EstadoOrdenEnum> activeStates = List.of(
                EstadoOrdenEnum.PENDIENTE,
                EstadoOrdenEnum.CONFIRMADO,
                EstadoOrdenEnum.PREPARANDO,
                EstadoOrdenEnum.LISTO
        );
        return ordenRepository.findByEstadoInOrderByFechaCreacionAsc(activeStates)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByClient(Long clientId) {
        return ordenRepository.findByUsuarioIdAndActivoTrueOrderByFechaCreacionDesc(clientId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByEmail(String email) {
        return ordenRepository.findOrdenesByEmail(email)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Orden orden = ordenRepository.findByNumeroOrden(orderNumber)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con número: " + orderNumber));
        return mapToOrderResponse(orden);
    }

    @Override
    public OrderResponse updateOrderDeliveryTime(Long id, Integer estimatedMinutes) {
        Orden orden = ordenRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
        
        orden.setTiempoEntregaEstimado(estimatedMinutes);
        Orden savedOrden = ordenRepository.save(orden);
        return mapToOrderResponse(savedOrden);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersPaginated(Pageable pageable) {
        return getAllOrders(pageable);
    }    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForDelivery() {
        return ordenRepository.findByEstadoOrderByFechaCreacionAsc(EstadoOrdenEnum.LISTO)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForPickup() {
        return ordenRepository.findByEstadoOrderByFechaCreacionAsc(EstadoOrdenEnum.LISTO)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    public void markOrderAsDelivered(Long id) {
        updateOrderStatus(id, EstadoOrdenEnum.ENTREGADO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrders(String searchTerm) {
        return ordenRepository.buscarOrdenes(searchTerm)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSales(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = ordenRepository.calcularVentasByFechaRango(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ordenRepository.countOrdenesByFechaRango(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateAverageTicket() {
        BigDecimal avg = ordenRepository.calcularTicketPromedio();
        return avg != null ? avg : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostSoldProducts(LocalDateTime startDate, LocalDateTime endDate) {
        // This would need a custom query in the repository
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Orden findOrderEntityById(Long id) {
        return ordenRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
    }

    @Override
    public String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "PIC" + timestamp + random;
    }

    @Override
    public BigDecimal calculateOrderTotal(OrderRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            total = total.add(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        return total;
    }

    @Override
    public void validateOrderRequest(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("La orden debe tener al menos un item");
        }
        
        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            if (item.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }
            if (item.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El precio debe ser mayor a 0");
            }
        }
    }

    @Override
    public void notifyOrderStatusChange(Long orderId, EstadoOrdenEnum newStatus) {
        log.info("Orden {} cambió a estado: {}", orderId, newStatus);
        // Implement notification logic here (email, SMS, push notifications, etc.)
    }    private boolean isValidStatusTransition(EstadoOrdenEnum currentStatus, EstadoOrdenEnum newStatus) {
        return switch (currentStatus) {
            case PENDIENTE -> newStatus == EstadoOrdenEnum.CONFIRMADO || newStatus == EstadoOrdenEnum.CANCELADO;
            case CONFIRMADO -> newStatus == EstadoOrdenEnum.PREPARANDO || newStatus == EstadoOrdenEnum.CANCELADO;
            case PREPARANDO -> newStatus == EstadoOrdenEnum.LISTO || newStatus == EstadoOrdenEnum.CANCELADO;
            case LISTO -> newStatus == EstadoOrdenEnum.ENTREGADO;
            case ENTREGADO, CANCELADO -> false; // Estados finales
        };
    }

    private OrderResponse mapToOrderResponse(Orden orden) {
        List<OrderResponse.OrderItemResponse> items = orden.getDetalles().stream()
                .map(detalle -> OrderResponse.OrderItemResponse.builder()
                        .id(detalle.getId())
                        .productoId(detalle.getProducto().getId())
                        .productoNombre(detalle.getProducto().getNombre())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .notas(detalle.getNotas())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(orden.getId())
                .numeroOrden(orden.getNumeroOrden())
                .usuarioId(orden.getUsuario().getId())
                .usuarioNombre(orden.getUsuario().getNombre())
                .usuarioEmail(orden.getUsuario().getEmail())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .direccionEntrega(orden.getDireccionEntrega())
                .telefonoContacto(orden.getTelefonoContacto())
                .metodoPago(orden.getMetodoPago())
                .notas(orden.getNotas())
                .items(items)
                .fechaCreacion(orden.getFechaCreacion())
                .fechaActualizacion(orden.getFechaActualizacion())
                .fechaEntrega(orden.getFechaEntrega())
                .build();
    }
}
