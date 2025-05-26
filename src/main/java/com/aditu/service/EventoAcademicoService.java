package com.aditu.service;

import com.aditu.model.*;
import com.aditu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EventoAcademicoService {
    @Autowired private EventoAcademicoRepository eventoRepository;
    public List<EventoAcademico> findAll() { return eventoRepository.findAll(); }
    public List<EventoAcademico> findByTutorId(Long id) { return eventoRepository.findByTutorId(id); }
    public EventoAcademico save(EventoAcademico e) { return eventoRepository.save(e); }
    public void delete(Long id) { eventoRepository.deleteById(id); }

    public List<EventoAcademico> buscarPorTituloODescripcion(String texto) {
        return eventoRepository.findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(texto, texto);
    }

}
