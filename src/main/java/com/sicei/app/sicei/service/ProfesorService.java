package com.sicei.app.sicei.service;

import com.sicei.app.sicei.exception.BusinessException;
import com.sicei.app.sicei.exception.RequestException;
import com.sicei.app.sicei.model.Profesor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesorService {

    private List<Profesor> profesores = new ArrayList<>();

    public List<Profesor> getAllProfesores() {
        return profesores;
    }

    public Profesor getProfesorById(Long id) {
        return profesores.stream()
                .filter(profesor -> profesor.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Profesor no encontrado", "R-003", HttpStatus.NOT_FOUND));
    }

    public Profesor saveProfesor(Profesor profesor) {
        validateProfesor(profesor);
        profesores.add(profesor);
        return profesor;
    }

    public Profesor updateProfesor(Long id, Profesor profesor) {
        validateProfesor(profesor);
        Profesor existingProfesor = getProfesorById(id);
        existingProfesor.setNumeroEmpleado(profesor.getNumeroEmpleado());
        existingProfesor.setNombres(profesor.getNombres());
        existingProfesor.setApellidos(profesor.getApellidos());
        existingProfesor.setHorasClase(profesor.getHorasClase());
        return existingProfesor;
    }

    public void deleteProfesor(Long id) {
        Profesor profesor = getProfesorById(id);
        profesores.remove(profesor);
    }

    private void validateProfesor(Profesor profesor) {
        if (profesor.getNumeroEmpleado() == null || profesor.getNumeroEmpleado().isEmpty()) {
            throw new RequestException("Numero de empleado es requerido", "B-002");
        }
        if (profesor.getNombres() == null || profesor.getNombres().isEmpty()) {
            throw new RequestException("Nombres es requerido", "B-002");
        }
        if (profesor.getApellidos() == null || profesor.getApellidos().isEmpty()) {
            throw new RequestException("Apellidos es requerido", "B-002");
        }
        if (profesor.getHorasClase() == null || profesor.getHorasClase() < 0) {
            throw new RequestException("Horas de clase debe ser un número positivo", "B-001");
        }
    }
}
