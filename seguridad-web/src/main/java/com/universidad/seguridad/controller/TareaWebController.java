package com.universidad.seguridad.controller;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.service.TareaService;
import com.universidad.seguridad.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tareas")
public class TareaWebController {

    private static final int MAX_TITULO = 150;
    private static final int MAX_DESCRIPCION = 500;

    private final TareaService tareas;
    private final UsuarioService usuarios;

    public TareaWebController(TareaService tareas, UsuarioService usuarios) {
        this.tareas = tareas;
        this.usuarios = usuarios;
    }

    // Lista las tareas del usuario autenticado; con ?q= las filtra por título
    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         Authentication auth, Model model) {
        Long usuarioId = usuarios.idDe(auth.getName());
        boolean hayBusqueda = q != null && !q.isBlank();
        model.addAttribute("tareas", hayBusqueda
            ? tareas.buscar(usuarioId, q)
            : tareas.listarDe(usuarioId));
        model.addAttribute("q", q);
        return "tareas";
    }

    @PostMapping
    public String crear(@RequestParam String titulo,
                        @RequestParam(required = false) String descripcion,
                        Authentication auth, RedirectAttributes redirect) {
        if (titulo.isBlank()) {
            redirect.addFlashAttribute("error", "El título es obligatorio");
        } else if (titulo.length() > MAX_TITULO
                || (descripcion != null && descripcion.length() > MAX_DESCRIPCION)) {
            redirect.addFlashAttribute("error", "El título admite hasta " + MAX_TITULO
                + " caracteres y la descripción hasta " + MAX_DESCRIPCION);
        } else {
            Tarea tarea = new Tarea();
            tarea.setTitulo(titulo.trim());
            tarea.setDescripcion(descripcion);
            tareas.crear(auth.getName(), tarea);
        }
        return "redirect:/tareas";
    }
}
