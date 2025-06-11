package com.piccola.tuproyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para PICCOLA
 * 
 * TODO: Implementar autenticación JWT
 * TODO: Configurar roles USER, ADMIN, KITCHEN
 * TODO: Proteger endpoints de administración
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y páginas públicas
                .requestMatchers("/css/**", "/js/**", "/imagenes/**").permitAll()
                .requestMatchers("/client/**", "/admin/css/**", "/admin/js/**").permitAll()
                .requestMatchers("/cliente/**", "/", "/inicio", "/menu", "/nosotros", "/contacto").permitAll()
                
                // API pública
                .requestMatchers("/api/auth/**", "/api/products/**").permitAll()
                
                // API protegida
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            );

        // TODO: Agregar filtro JWT aquí
        
        return http.build();
    }
}
