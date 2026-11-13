package com.universidad.seguridad.repository;

import com.universidad.seguridad.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByPropietarioId(Long usuarioId);

    // SEGURO: @Query con parámetros nombrados — JPA los envía como
    // PreparedStatement, el valor nunca se interpreta como JPQL/SQL.
    // La búsqueda se limita a las tareas del usuario para que nadie
    // pueda leer las de otros.
    @Query("SELECT t FROM Tarea t WHERE t.propietario.id = :usuarioId "
         + "AND t.titulo LIKE %:texto%")
    List<Tarea> buscarPorTitulo(@Param("usuarioId") Long usuarioId,
                                @Param("texto") String texto);

    // INSEGURO — NUNCA HACER ESTO (solo referencia en comentario):
    // @Query(value = "SELECT * FROM tareas WHERE titulo LIKE '%" +
    //     texto + "%'", nativeQuery = true)
    // Si texto = "' OR '1'='1", la consulta devuelve TODAS las tareas
    // sin filtrar, ignorando por completo el criterio de búsqueda.
}
