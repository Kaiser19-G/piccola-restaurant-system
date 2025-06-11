package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.request.LoginRequest;
import com.piccola.tuproyecto.dto.request.RegisterRequest;
import com.piccola.tuproyecto.dto.response.AuthResponse;
import com.piccola.tuproyecto.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST para autenticación de usuarios y empleados
 * 
 * Endpoints:
 * - POST /api/auth/login - Login de usuarios
 * - POST /api/auth/register - Registro de clientes
 * - POST /api/auth/admin/login - Login de empleados
 * - POST /api/auth/logout - Cerrar sesión
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    // TODO: Inyectar AuthService
    // private final AuthService authService;

    /**
     * Login de clientes
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // TODO: Implementar autenticación de cliente
        // AuthResponse response = authService.loginCliente(request);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token("dummy-jwt-token")
                .type("Bearer")
                .username(request.getUsername())
                .roles(new String[]{"USER"})
                .build());
    }

    /**
     * Registro de nuevos clientes
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        // TODO: Implementar registro de cliente
        // authService.registerCliente(request);
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Cliente registrado exitosamente")
                .build());
    }

    /**
     * Login de empleados/administradores
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        // TODO: Implementar autenticación de empleado
        // AuthResponse response = authService.loginEmpleado(request);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token("dummy-admin-jwt-token")
                .type("Bearer")
                .username(request.getUsername())
                .roles(new String[]{"ADMIN"})
                .build());
    }

    /**
     * Cerrar sesión (invalidar token)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout() {
        // TODO: Invalidar token JWT
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Sesión cerrada exitosamente")
                .build());
    }

    /**
     * Verificar validez del token
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyToken() {
        // TODO: Verificar token JWT del header
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Token válido")
                .build());
    }
}
