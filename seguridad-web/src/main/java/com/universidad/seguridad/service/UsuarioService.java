package com.universidad.seguridad.service;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @Transactional
    public void registrar(Usuario usuario) {
        if (repo.existsByEmail(usuario.getEmail()))
            throw new IllegalStateException("El correo ya está registrado");
        // El formulario enlaza todos los campos de Usuario: sin esto, un POST
        // con id=1 sobrescribiría una cuenta existente y activo/rol podrían
        // venir del cliente
        usuario.setId(null);
        usuario.setActivo(true);
        usuario.setContrasenia(encoder.encode(usuario.getContrasenia()));
        usuario.setRol("ROLE_USER"); // rol por defecto
        repo.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + email));
    }

    // Lo usan las expresiones de @PreAuthorize: funciona igual para la sesión
    // y para JWT, porque ambos exponen el correo como authentication.name
    public Long idDe(String email) {
        return repo.findByEmail(email).map(Usuario::getId).orElse(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarTodos() { return repo.findAll(); }
}
