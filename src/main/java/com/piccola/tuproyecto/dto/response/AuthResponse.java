package com.piccola.tuproyecto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para response de autenticación (login exitoso)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private String email;
    private String[] roles;
    private String role; // Single role for current user
    
    // Información adicional del usuario
    private String nombre;
    private String apellido;
    
    // Tiempo de expiración del token en segundos
    private Long expiresIn;
    
    // Información adicional para el frontend
    @Builder.Default
    private boolean firstLogin = false;
    @Builder.Default
    private boolean mustChangePassword = false;
}
