package com.universidad.seguridad.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRACION_MS = 3_600_000; // 1 hora

    private final SecretKey clave;

    // La clave se lee de application.properties (jwt.secret), que a su vez
    // la toma de la variable de entorno JWT_SECRET. Si tiene menos de 32
    // bytes, la aplicación falla al arrancar en lugar de firmar con una
    // clave débil.
    public JwtUtil(@Value("${jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String email, String rol) {
        return Jwts.builder()
            .subject(email)
            .claim("rol", rol)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRACION_MS))
            .signWith(clave)
            .compact();
    }

    public String extraerEmail(String token) {
        return parsearClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return parsearClaims(token).get("rol", String.class);
    }

    public boolean esValido(String token) {
        try {
            parsearClaims(token); // lanza excepción si la firma o la
                                   // expiración no son válidas
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // firma inválida, token expirado o malformado
        }
    }

    private Claims parsearClaims(String token) {
        return Jwts.parser()
            .verifyWith(clave)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
