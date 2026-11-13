package com.universidad.seguridad.controller;

import com.universidad.seguridad.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public ApiAuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public record LoginRequest(String email, String contrasenia) {}
    public record LoginResponse(String token, String email, String rol) {}

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        // Reutiliza el MISMO AuthenticationManager/UserDetailsService/
        // BCryptPasswordEncoder de la Parte 1: las credenciales se
        // validan exactamente igual, solo cambia lo que se devuelve
        // al final (un token en vez de una cookie de sesión).
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.email(), req.contrasenia())
        );
        String rol = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generarToken(req.email(), rol);
        return new LoginResponse(token, req.email(), rol);
    }
}
