package com.sicei.app.sicei.controller;

import com.sicei.app.sicei.model.Profesor;
import com.sicei.app.sicei.service.ProfesorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profesores")
public class ProfesorController {

    private final ProfesorService profesorService;

    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    public ResponseEntity<List<Profesor>> getAllProfesores() {
        List<Profesor> profesores = profesorService.getAllProfesores();
        return new ResponseEntity<>(profesores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profesor> getProfesorById(@PathVariable Long id) {
        Profesor profesor = profesorService.getProfesorById(id);
        return new ResponseEntity<>(profesor, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Profesor> saveProfesor(@RequestBody Profesor profesor) {
        Profesor savedProfesor = profesorService.saveProfesor(profesor);
        return new ResponseEntity<>(savedProfesor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profesor> updateProfesor(@PathVariable Long id, @RequestBody Profesor profesor) {
        Profesor updatedProfesor = profesorService.updateProfesor(id, profesor);
        return new ResponseEntity<>(updatedProfesor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesor(@PathVariable Long id) {
        profesorService.deleteProfesor(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
