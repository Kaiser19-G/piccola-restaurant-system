package com.piccola.tuproyecto.entity.enums;

/**
 * Enum para los estados de las órdenes en Hamburguesería PICCOLA
 */
public enum EstadoOrdenEnum {
    PENDIENTE("Pendiente", "⏳", "La orden está pendiente de confirmación"),
    CONFIRMADO("Confirmado", "✅", "La orden ha sido confirmada"),
    PREPARANDO("Preparando", "👨‍🍳", "El pedido se está preparando en cocina"),
    LISTO("Listo", "🔔", "El pedido está listo para entregar/recoger"),
    ENTREGADO("Entregado", "📦", "El pedido ha sido entregado al cliente"),
    CANCELADO("Cancelado", "❌", "La orden ha sido cancelada");
    
    private final String nombre;
    private final String icono;
    private final String descripcion;
    
    EstadoOrdenEnum(String nombre, String icono, String descripcion) {
        this.nombre = nombre;
        this.icono = icono;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getIcono() {
        return icono;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getDisplayName() {
        return icono + " " + nombre;
    }
    
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
    
    public boolean esActivo() {
        return this == PENDIENTE || this == CONFIRMADO || this == PREPARANDO || this == LISTO;
    }
}
