package com.piccola.tuproyecto.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {
    
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18"); // 18% IGV en Perú
    private static final BigDecimal DELIVERY_BASE_COST = new BigDecimal("5.00");
    
    public static BigDecimal calculateSubtotal(BigDecimal price, Integer quantity) {
        if (price == null || quantity == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return price.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculateIGV(BigDecimal subtotal) {
        if (subtotal == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal taxes, BigDecimal deliveryCost, BigDecimal discount) {
        BigDecimal total = subtotal != null ? subtotal : BigDecimal.ZERO;
        
        if (taxes != null) {
            total = total.add(taxes);
        }
        
        if (deliveryCost != null) {
            total = total.add(deliveryCost);
        }
        
        if (discount != null) {
            total = total.subtract(discount);
        }
        
        return total.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculateDeliveryCost(String district, BigDecimal orderTotal) {
        // Entrega gratis para órdenes mayores a 50 soles
        if (orderTotal.compareTo(new BigDecimal("50.00")) >= 0) {
            return BigDecimal.ZERO;
        }
        
        // Costo base de entrega
        return DELIVERY_BASE_COST;
    }
    
    public static BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        if (amount == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discountDecimal = discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return amount.multiply(discountDecimal).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal formatPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
