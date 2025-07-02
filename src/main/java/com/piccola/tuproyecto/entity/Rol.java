package com.piccola.tuproyecto.entity;

/**
 * Enum que define los roles de usuario en el sistema PICCOLA
 */
public enum Rol {
    CLIENTE("Cliente"),
    EMPLEADO("Empleado"),
    ADMIN("Administrador");

    private final String displayName;

    Rol(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Verifica si el rol tiene permisos administrativos
     */
    public boolean isAdministrativo() {
        return this == ADMIN || this == EMPLEADO;
    }

    /**
     * Verifica si el rol puede acceder al panel de administración
     */
    public boolean canAccessAdminPanel() {
        return this == ADMIN;
    }

    /**
     * Verifica si el rol puede gestionar productos
     */
    public boolean canManageProducts() {
        return this == ADMIN || this == EMPLEADO;
    }

    /**
     * Verifica si el rol puede registrar otros usuarios
     */
    public boolean canRegisterUsers() {
        return this == ADMIN;
    }
}
