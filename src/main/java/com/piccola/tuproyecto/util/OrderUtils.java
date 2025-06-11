package com.piccola.tuproyecto.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderUtils {
    
    private static final String ORDER_PREFIX = "PIC";
    private static final Random random = new Random();
    
    public static String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        int randomNum = random.nextInt(999) + 1;
        return ORDER_PREFIX + timestamp + String.format("%03d", randomNum);
    }
    
    public static LocalDateTime calculateEstimatedDeliveryTime(int preparationMinutes, boolean isDelivery) {
        LocalDateTime now = LocalDateTime.now();
        int totalMinutes = preparationMinutes;
        
        if (isDelivery) {
            totalMinutes += 30; // Tiempo adicional para entrega
        }
        
        return now.plusMinutes(totalMinutes);
    }
    
    public static boolean isOrderInDeliveryWindow() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        // Horario de entrega de 10:00 AM a 10:00 PM
        return hour >= 10 && hour < 22;
    }
    
    public static String formatOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.length() < 6) {
            return orderNumber;
        }
        
        // Format: PIC-YYMMDDHHMM-XXX
        String prefix = orderNumber.substring(0, 3);
        String timestamp = orderNumber.substring(3, orderNumber.length() - 3);
        String suffix = orderNumber.substring(orderNumber.length() - 3);
        
        return prefix + "-" + timestamp + "-" + suffix;
    }
}
