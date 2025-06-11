package com.piccola.tuproyecto.service;

import com.piccola.tuproyecto.dto.request.OrderRequest;
import com.piccola.tuproyecto.dto.response.OrderResponse;
import com.piccola.tuproyecto.entity.Orden;
import com.piccola.tuproyecto.entity.enums.EstadoOrdenEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    
    // User-facing methods
    OrderResponse createOrder(OrderRequest request, String userEmail);
    
    List<OrderResponse> getUserOrders(String userEmail);
    
    OrderResponse getOrderById(Long id, String userEmail);
    
    OrderResponse cancelOrder(Long id, String userEmail);
    
    // Admin methods
    OrderResponse getOrderById(Long id);
    
    OrderResponse getOrderByIdAdmin(Long orderId);
    
    Page<OrderResponse> getAllOrders(Pageable pageable);
    
    Page<OrderResponse> getOrdersByStatus(EstadoOrdenEnum estado, Pageable pageable);
    
    Page<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    OrderResponse updateOrderStatus(Long orderId, EstadoOrdenEnum nuevoEstado);
    
    void deleteOrder(Long orderId);
    
    List<OrderResponse> searchOrders(String searchTerm);
    
    // Statistics methods
    BigDecimal getTotalSales();
    
    Long getTotalOrdersCount();
    
    Long getOrdersCountByStatus(EstadoOrdenEnum estado);
    
    BigDecimal calculateTotalSales(LocalDateTime startDate, LocalDateTime endDate);
    
    Long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    BigDecimal calculateAverageTicket();
    
    List<Object[]> getMostSoldProducts(LocalDateTime startDate, LocalDateTime endDate);
    
    // Delivery management
    List<OrderResponse> getOrdersForDelivery();
    
    List<OrderResponse> getOrdersForPickup();
    
    void markOrderAsDelivered(Long id);
    
    List<OrderResponse> getActiveOrders();
    
    List<OrderResponse> getOrdersByClient(Long clientId);
    
    List<OrderResponse> getOrdersByEmail(String email);
    
    OrderResponse getOrderByNumber(String orderNumber);
    
    OrderResponse updateOrderDeliveryTime(Long id, Integer estimatedMinutes);
    
    Page<OrderResponse> getOrdersPaginated(Pageable pageable);
    
    // Internal methods
    Orden findOrderEntityById(Long id);
    
    String generateOrderNumber();
    
    BigDecimal calculateOrderTotal(OrderRequest request);
    
    void validateOrderRequest(OrderRequest request);
    
    void notifyOrderStatusChange(Long orderId, EstadoOrdenEnum newStatus);
}
