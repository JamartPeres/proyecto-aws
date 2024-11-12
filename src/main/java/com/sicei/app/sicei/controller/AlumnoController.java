package com.sicei.app.sicei.controller;

import com.sicei.app.sicei.model.Alumno;
import com.sicei.app.sicei.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public ResponseEntity<List<Alumno>> getAllAlumnos() {
        List<Alumno> alumnos = alumnoService.getAllAlumnos();
        return new ResponseEntity<>(alumnos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable Long id) {
        Alumno alumno = alumnoService.getAlumnoById(id);
        return new ResponseEntity<>(alumno, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Alumno> saveAlumno(@RequestBody Alumno alumno) {
        Alumno savedAlumno = alumnoService.saveAlumno(alumno);
        return new ResponseEntity<>(savedAlumno, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> updateAlumno(@PathVariable Long id, @RequestBody Alumno alumno) {
        Alumno updatedAlumno = alumnoService.updateAlumno(id, alumno);
        return new ResponseEntity<>(updatedAlumno, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlumno(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
