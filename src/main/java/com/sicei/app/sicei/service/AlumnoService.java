package com.sicei.app.sicei.service;

import com.sicei.app.sicei.exception.BusinessException;
import com.sicei.app.sicei.exception.RequestException;
import com.sicei.app.sicei.model.Alumno;
import com.sicei.app.sicei.repository.AlumnoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public List<Alumno> getAllAlumnos() {
        return alumnoRepository.findAll();
    }

    public Alumno getAlumnoById(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Alumno no encontrado", "R-003", HttpStatus.NOT_FOUND));
    }

    public Alumno saveAlumno(Alumno alumno) {
        validateAlumno(alumno);
        return alumnoRepository.save(alumno);
    }

    public Alumno updateAlumno(Long id, Alumno alumno) {
        validateAlumno(alumno);
        Alumno existingAlumno = getAlumnoById(id);
        existingAlumno.setNombres(alumno.getNombres());
        existingAlumno.setApellidos(alumno.getApellidos());
        existingAlumno.setMatricula(alumno.getMatricula());
        existingAlumno.setPromedio(alumno.getPromedio());
        return alumnoRepository.save(existingAlumno);
    }

    public void deleteAlumno(Long id) {
        Alumno alumno = getAlumnoById(id);
        alumnoRepository.delete(alumno);
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
