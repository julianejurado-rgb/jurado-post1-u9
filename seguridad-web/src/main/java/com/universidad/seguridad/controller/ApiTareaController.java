package com.universidad.seguridad.controller;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.service.TareaService;
import com.universidad.seguridad.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class ApiTareaController {

    private final TareaService service;
    private final UsuarioService usuarios;

    public ApiTareaController(TareaService service, UsuarioService usuarios) {
        this.service = service;
        this.usuarios = usuarios;
    }

    @GetMapping
    public List<Tarea> misTareas(Authentication auth) {
        // Authentication llega poblado por JwtAuthFilter, exactamente
        // como llegaría si la petición viniera autenticada por sesión:
        // el resto de la aplicación no necesita saber cuál mecanismo
        // se usó para autenticar a este usuario.
        Long usuarioId = usuarios.idDe(auth.getName());
        if (usuarioId == null) {
            // El token es válido pero el usuario ya no existe
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return service.listarDe(usuarioId);
    }
}
