package com.piccola.tuproyecto.dto.response;

import com.piccola.tuproyecto.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de datos del usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private Rol rol;
    private Boolean activo;
    private Boolean emailVerificado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getNombreCompleto() {
        if (apellido != null && !apellido.trim().isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }

    /**
     * Obtiene el nombre del rol para mostrar
     */
    public String getRolDisplay() {
        return rol != null ? rol.getDisplayName() : "Sin rol";
    }
}
