package com.piccola.tuproyecto.controller;

import com.piccola.tuproyecto.dto.request.RegisterRequest;
import com.piccola.tuproyecto.entity.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para páginas de autenticación
 */
@Controller
@RequiredArgsConstructor
public class AuthViewController {

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }
        
        return "auth/login";
    }

    /**
     * Página de registro para clientes
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("isAdminRegister", false);
        return "auth/register";
    }

    /**
     * Página de registro administrativo (solo para ADMIN)
     */
    @GetMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("isAdminRegister", true);
        model.addAttribute("roles", Rol.values());
        return "auth/admin-register";
    }

    /**
     * Página de dashboard para admin
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }

    /**
     * Página de panel para empleados
     */
    @GetMapping("/empleado/panel")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String empleadoPanel(Model model) {
        return "empleado/panel";
    }

    /**
     * Página de perfil para clientes
     */
    @GetMapping("/cliente/perfil")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public String clientePerfil(Model model) {
        return "cliente/perfil";
    }
}
