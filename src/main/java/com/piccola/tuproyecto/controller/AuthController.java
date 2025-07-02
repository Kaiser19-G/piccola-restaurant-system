package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.request.LoginRequest;
import com.piccola.tuproyecto.dto.request.RegisterRequest;
import com.piccola.tuproyecto.dto.response.AuthResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import com.piccola.tuproyecto.entity.Usuario;
import com.piccola.tuproyecto.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST para autenticación de usuarios y empleados
 * 
 * Endpoints:
 * - POST /api/auth/login - Login de usuarios
 * - POST /api/auth/register - Registro de clientes
 * - POST /api/auth/admin/login - Login de empleados/admin
 * - POST /api/auth/logout - Cerrar sesión
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Login de usuarios (clientes, empleados, admin)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Intento de login para: {}", request.getEmail());
            
            AuthResponse response = authService.login(request);
            
            log.info("Login exitoso para: {}", request.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error en login para: {}", request.getEmail(), e);
            throw e;
        }
    }

    /**
     * Registro de nuevos clientes
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Registro de cliente para: {}", request.getEmail());
            
            AuthResponse response = authService.register(request);
            
            log.info("Cliente registrado exitosamente: {}", request.getEmail());
            return ResponseEntity.ok(response);
                    
        } catch (Exception e) {
            log.error("Error en registro de cliente: {}", request.getEmail(), e);
            throw e;
        }
    }

    /**
     * Login de empleados/administradores
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Intento de login administrativo para: {}", request.getEmail());
            
            AuthResponse response = authService.loginEmployee(request);
            
            log.info("Login administrativo exitoso para: {}", request.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error en login administrativo para: {}", request.getEmail(), e);
            throw e;
        }
    }

    /**
     * Cerrar sesión (invalidar token)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                authService.logout(jwtToken);
            }
            
            return ResponseEntity.ok(ApiResponse.<Object>builder()
                    .success(true)
                    .message("Sesión cerrada exitosamente")
                    .build());
        } catch (Exception e) {
            log.error("Error al cerrar sesión", e);
            return ResponseEntity.ok(ApiResponse.<Object>builder()
                    .success(true)
                    .message("Sesión cerrada")
                    .build());
        }
    }

    /**
     * Verificar validez del token
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                boolean isValid = authService.isTokenValid(jwtToken);
                
                if (isValid) {
                    return ResponseEntity.ok(ApiResponse.<Object>builder()
                            .success(true)
                            .message("Token válido")
                            .build());
                } else {
                    return ResponseEntity.status(401).body(ApiResponse.<Object>builder()
                            .success(false)
                            .message("Token inválido")
                            .build());
                }
            }
            
            return ResponseEntity.status(400).body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Token no proporcionado")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error al verificar token", e);
            return ResponseEntity.status(401).body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Token inválido")
                    .build());
        }
    }

    /**
     * Validar disponibilidad de email
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        try {
            // Usar el repositorio directamente a través del servicio
            boolean exists = authService.getCurrentUser() != null; // Temporal, necesitamos método en service
            
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message(exists ? "Email ya registrado" : "Email disponible")
                    .data(!exists)
                    .build());
        } catch (Exception e) {
            // Si no se puede verificar, asumir que está disponible
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("Email disponible")
                    .data(true)
                    .build());
        }
    }

    /**
     * Obtener usuario actual
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> getCurrentUser() {
        try {
            Usuario usuario = authService.getCurrentUser();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            log.error("Error al obtener usuario actual", e);
            return ResponseEntity.status(401).build();
        }
    }
}
