package com.sicei.app.sicei.service;

import com.sicei.app.sicei.exception.BusinessException;
import com.sicei.app.sicei.exception.RequestException;
import com.sicei.app.sicei.model.Alumno;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {

    private List<Alumno> alumnos = new ArrayList<>();

    public List<Alumno> getAllAlumnos() {
        return alumnos;
    }

    public Alumno getAlumnoById(Long id) {
        return alumnos.stream()
                .filter(alumno -> alumno.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Alumno no encontrado", "R-003", HttpStatus.NOT_FOUND));
    }

    public Alumno saveAlumno(Alumno alumno) {
        validateAlumno(alumno);
        alumnos.add(alumno);
        return alumno;
    }

    public Alumno updateAlumno(Long id, Alumno alumno) {
        validateAlumno(alumno);
        Alumno existingAlumno = getAlumnoById(id);
        existingAlumno.setNombres(alumno.getNombres());
        existingAlumno.setApellidos(alumno.getApellidos());
        existingAlumno.setMatricula(alumno.getMatricula());
        existingAlumno.setPromedio(alumno.getPromedio());
        return existingAlumno;
    }

    public void deleteAlumno(Long id) {
        Alumno alumno = getAlumnoById(id);
        alumnos.remove(alumno);
    }

    private void validateAlumno(Alumno alumno) {
        if (alumno.getNombres() == null || alumno.getNombres().isEmpty()) {
            throw new RequestException("Nombres es requerido", "B-002");
        }
        if (alumno.getApellidos() == null || alumno.getApellidos().isEmpty()) {
            throw new RequestException("Apellidos es requerido", "B-002");
        }
        if (alumno.getMatricula() == null || alumno.getMatricula().isEmpty()) {
            throw new RequestException("Matricula es requerido", "B-002");
        }
        if (alumno.getPromedio() == null || alumno.getPromedio() < 0.0 || alumno.getPromedio() > 10.0) {
            throw new RequestException("Promedio debe estar entre 0.0 y 10.0", "B-001");
        }
    }
}
