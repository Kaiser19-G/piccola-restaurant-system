package com.piccola.tuproyecto.service;

import com.piccola.tuproyecto.dto.request.LoginRequest;
import com.piccola.tuproyecto.dto.request.RegisterRequest;
import com.piccola.tuproyecto.dto.response.AuthResponse;
import com.piccola.tuproyecto.entity.Usuario;

public interface AuthService {
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse loginEmployee(LoginRequest request);
    
    void logout(String token);
    
    boolean isTokenValid(String token);
    
    Usuario getCurrentUser();
    
    String generatePasswordResetToken(String email);
    
    boolean resetPassword(String token, String newPassword);
    
    boolean changePassword(String currentPassword, String newPassword);
}
