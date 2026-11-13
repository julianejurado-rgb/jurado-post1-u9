package com.universidad.seguridad.service;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.repository.TareaRepository;
import com.universidad.seguridad.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TareaService {
    private final TareaRepository repo;
    private final UsuarioRepository usuarios;

    public TareaService(TareaRepository repo, UsuarioRepository usuarios) {
        this.repo = repo;
        this.usuarios = usuarios;
    }

    // ADMIN ve las de cualquiera; USER solo las suyas. El id del usuario
    // autenticado se obtiene por su correo (authentication.name), que es
    // igual en la sesión y en JWT.
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.idDe(authentication.name) == #usuarioId")
    public List<Tarea> listarDe(Long usuarioId) {
        return repo.findByPropietarioId(usuarioId);
    }

    @PreAuthorize("hasRole('ADMIN') or @usuarioService.idDe(authentication.name) == #usuarioId")
    public List<Tarea> buscar(Long usuarioId, String texto) {
        return repo.buscarPorTitulo(usuarioId, texto); // @Query parametrizado — seguro
    }

    @Transactional
    public Tarea crear(String emailPropietario, Tarea tarea) {
        tarea.setId(null);
        tarea.setCompletada(false);
        tarea.setPropietario(usuarios.findByEmail(emailPropietario)
            .orElseThrow(() -> new IllegalStateException(
                "Usuario no encontrado: " + emailPropietario)));
        return repo.save(tarea);
    }
}
