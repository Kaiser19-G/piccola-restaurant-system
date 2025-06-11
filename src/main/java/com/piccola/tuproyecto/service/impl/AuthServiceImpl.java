package com.piccola.tuproyecto.service.impl;

import com.piccola.tuproyecto.dto.request.LoginRequest;
import com.piccola.tuproyecto.dto.request.RegisterRequest;
import com.piccola.tuproyecto.dto.response.AuthResponse;
import com.piccola.tuproyecto.entity.Usuario;
import com.piccola.tuproyecto.exception.BadRequestException;
import com.piccola.tuproyecto.exception.UnauthorizedException;
import com.piccola.tuproyecto.repository.UsuarioRepository;
import com.piccola.tuproyecto.security.JwtService;
import com.piccola.tuproyecto.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = jwtService.generateTokenWithRole(usuario, usuario.getRole().name());

            return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .role(usuario.getRole().name())
                .build();

        } catch (Exception e) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setRole(Usuario.Role.USER);
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateTokenWithRole(usuario, usuario.getRole().name());

        return AuthResponse.builder()
            .token(token)
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .role(usuario.getRole().name())
            .build();
    }

    @Override
    public AuthResponse loginEmployee(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

            // Verificar que sea empleado o admin
            if (usuario.getRole() == Usuario.Role.USER) {
                throw new UnauthorizedException("Acceso denegado - Solo empleados");
            }

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = jwtService.generateTokenWithRole(usuario, usuario.getRole().name());

            return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .role(usuario.getRole().name())
                .build();

        } catch (Exception e) {
            throw new UnauthorizedException("Credenciales inválidas o acceso denegado");
        }
    }

    @Override
    public void logout(String token) {
        // TODO: Implementar blacklist de tokens si es necesario
        SecurityContextHolder.clearContext();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
                .orElse(null);
            return usuario != null && jwtService.isTokenValid(token, usuario);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmailAndActivoTrue(email)
            .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
    }

    @Override
    public String generatePasswordResetToken(String email) {
        // TODO: Implementar reset de contraseña
        throw new UnsupportedOperationException("Funcionalidad no implementada aún");
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        // TODO: Implementar reset de contraseña
        throw new UnsupportedOperationException("Funcionalidad no implementada aún");
    }

    @Override
    public boolean changePassword(String currentPassword, String newPassword) {
        Usuario usuario = getCurrentUser();
        
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new BadRequestException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
        
        return true;
    }
}
