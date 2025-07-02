package com.piccola.tuproyecto.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utilidad para manejo de tokens JWT
 */
@Component
@Slf4j
public class JWTUtil {

    @Value("${app.jwt.secret:piccolaSecretKeyForJWTTokenGeneration2025VerySecureKey}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400}") // 24 horas en segundos
    private int jwtExpirationInSeconds;

    /**
     * Genera un token JWT para el usuario
     */
    public String generateToken(String email, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene el email del token
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error al obtener email del token", e);
            throw new RuntimeException("Token inválido");
        }
    }

    /**
     * Obtiene el rol del token
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error al obtener rol del token", e);
            throw new RuntimeException("Token inválido");
        }
    }

    /**
     * Valida si el token es válido
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Token no soportado: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token malformado: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validando token", e);
            return false;
        }
    }

    /**
     * Verifica si el token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Obtiene la fecha de expiración del token
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Error al obtener fecha de expiración del token", e);
            return null;
        }
    }

    /**
     * Genera un token de refresh (con mayor duración)
     */
    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(7, ChronoUnit.DAYS); // 7 días

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene los claims del token
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
