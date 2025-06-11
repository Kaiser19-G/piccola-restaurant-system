package com.piccola.tuproyecto.entity.enums;

/**
 * Enum para las categorías de productos de Hamburguesería PICCOLA
 */
public enum CategoriaEnum {
    HAMBURGUESAS("Hamburguesas", "🧀"),
    SALCHIPAPAS("Salchipapas", "🍟"),
    BROASTERS("Broaster's", "🍗"),
    BEBIDAS("Bebidas", "🥤"),
    ADICIONALES("Adicionales", "➕"),
    CERVEZAS("Cervezas", "🍺"),
    CIGARROS("Cigarros", "🚬");
    
    private final String nombre;
    private final String icono;
    
    CategoriaEnum(String nombre, String icono) {
        this.nombre = nombre;
        this.icono = icono;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getIcono() {
        return icono;
    }
    
    public String getDisplayName() {
        return icono + " " + nombre;
    }
}
