package com.piccola.tuproyecto.dto.request;

import com.piccola.tuproyecto.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * DTO para request de registro de nuevos usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "Confirmar contraseña es obligatorio")
    private String confirmPassword;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;

    // Dirección opcional para delivery
    private String direccion;

    // Solo usado en registro administrativo
    private Rol rol;

    // Aceptación de términos y condiciones
    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean aceptaTerminos;

    /**
     * Valida que las contraseñas coincidan
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Establece rol por defecto como CLIENTE si no se especifica
     */
    public Rol getRol() {
        return rol != null ? rol : Rol.CLIENTE;
    }

    /**
     * Obtiene nombre completo
     */
    public String getNombreCompleto() {
        if (apellido != null && !apellido.trim().isEmpty()) {
            return nombre + " " + apellido;
        }
        return nombre;
    }
}
