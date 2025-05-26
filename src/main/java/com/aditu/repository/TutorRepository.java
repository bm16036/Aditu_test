package com.aditu.repository;

import com.aditu.model.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    // Método personalizado para buscar por nombres o apellidos (sin distinguir mayúsculas/minúsculas)
    List<Tutor> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
    Optional<Tutor> findByUsername(String username);
    Optional<Tutor> findByCorreoElectronico(String correoElectronico);
}