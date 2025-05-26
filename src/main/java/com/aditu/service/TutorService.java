package com.aditu.service;

import com.aditu.model.*;
import com.aditu.repository.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TutorService(TutorRepository tutorRepository, BCryptPasswordEncoder passwordEncoder) {
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tutor registerTutor(Tutor tutor) {
        if (tutor.getCorreoElectronico() == null || tutor.getCorreoElectronico().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (tutor.getPassword() == null || tutor.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    
        System.out.println("Contraseña antes de cifrar: " + tutor.getPassword()); // Log para depuración
    
        if (tutorRepository.findByCorreoElectronico(tutor.getCorreoElectronico()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
    
        // Generar un username temporal si no se proporciona
        if (tutor.getUsername() == null || tutor.getUsername().isEmpty()) {
            tutor.setUsername("tutor_" + UUID.randomUUID().toString().substring(0, 8));
        }
    
        tutor.setPassword(passwordEncoder.encode(tutor.getPassword())); // Cifrar la contraseña
        return tutorRepository.save(tutor); // Guardar en la base de datos
    }

    public Optional<Tutor> authenticateTutor(String usernameOrEmail, String password) {
        Optional<Tutor> tutor = tutorRepository.findByUsername(usernameOrEmail)
                .or(() -> tutorRepository.findByCorreoElectronico(usernameOrEmail));
        if (tutor.isPresent() && passwordEncoder.matches(password, tutor.get().getPassword())) {
            return tutor;
        }
        return Optional.empty();
    }

    public Optional<Tutor> getTutorById(Long id) {
        return tutorRepository.findById(id);
    }

    public List<Tutor> findAll() { return tutorRepository.findAll(); }
    public Tutor save(Tutor tutor) { return tutorRepository.save(tutor); }
    public void delete(Long id) { tutorRepository.deleteById(id); }
    public List<Tutor> findByNombresOrApellidos(String searchTerm) {
        return tutorRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(searchTerm, searchTerm);
    }

    public Tutor getCurrentTutor() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Tutor) {
            return (Tutor) principal;
        } else {
            throw new IllegalStateException("No hay un tutor autenticado");
        }
    }

    public void updateProfile(String nombres, String apellidos, String username) {
        Tutor currentTutor = getCurrentTutor(); // Implementa este método para obtener el tutor logueado
        if (username != null && tutorRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        currentTutor.setNombres(nombres);
        currentTutor.setApellidos(apellidos);
        currentTutor.setUsername(username);
        tutorRepository.save(currentTutor);
    }

}