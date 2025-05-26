package com.aditu.repository;

import com.aditu.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoAcademicoRepository extends JpaRepository<EventoAcademico, Long> {
    List<EventoAcademico> findByTutorId(Long tutorId);
    List<EventoAcademico> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo, String descripcion);
}