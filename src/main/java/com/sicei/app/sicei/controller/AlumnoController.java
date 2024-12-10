package com.sicei.app.sicei.controller;

import com.sicei.app.sicei.model.Alumno;
import com.sicei.app.sicei.service.AlumnoService;
import com.sicei.app.sicei.service.DynamoDbService;
import com.sicei.app.sicei.service.S3Service;
import com.sicei.app.sicei.service.SnsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final S3Service s3Service;
    private final SnsService snsService;
    private final DynamoDbService dynamoDbService;

    public AlumnoController(AlumnoService alumnoService, S3Service s3Service, SnsService snsService, DynamoDbService dynamoDbService) {
        this.alumnoService = alumnoService;
        this.s3Service = s3Service;
        this.snsService = snsService;
        this.dynamoDbService = dynamoDbService;
    }

    // Obtener todos los alumnos
    @GetMapping
    public ResponseEntity<List<Alumno>> getAllAlumnos() {
        List<Alumno> alumnos = alumnoService.getAllAlumnos();
        return new ResponseEntity<>(alumnos, HttpStatus.OK);
    }

    // Obtener un alumno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable Long id) {
        Alumno alumno = alumnoService.getAlumnoById(id);
        return new ResponseEntity<>(alumno, HttpStatus.OK);
    }

    // Crear un nuevo alumno
    @PostMapping
    public ResponseEntity<Alumno> saveAlumno(@RequestBody Alumno alumno) {
        Alumno savedAlumno = alumnoService.saveAlumno(alumno);
        return new ResponseEntity<>(savedAlumno, HttpStatus.CREATED);
    }

    // Actualizar un alumno existente
    @PutMapping("/{id}")
    public ResponseEntity<Alumno> updateAlumno(@PathVariable Long id, @RequestBody Alumno alumno) {
        Alumno updatedAlumno = alumnoService.updateAlumno(id, alumno);
        return new ResponseEntity<>(updatedAlumno, HttpStatus.OK);
    }

    // Eliminar un alumno
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlumno(@PathVariable Long id) {
        alumnoService.deleteAlumno(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Subir foto de perfil para un alumno
    @PostMapping("/{id}/fotoPerfil")
    public ResponseEntity<Map<String, Object>> uploadFotoPerfil(@PathVariable Long id, @RequestParam("foto") MultipartFile file) {
        Alumno alumno = alumnoService.getAlumnoById(id);

        try {
            // Crear archivo temporal
            Path tempFile = Files.createTempFile("fotoPerfil", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Subir el archivo al bucket de S3
            String fotoPerfilUrl = s3Service.uploadFile(tempFile, "fotoPerfil/" + file.getOriginalFilename());
            alumno.setFotoPerfilUrl(fotoPerfilUrl);
            alumnoService.saveAlumno(alumno);

            // Respuesta de éxito
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Foto de perfil subida con éxito");
            response.put("fotoPerfilUrl", fotoPerfilUrl);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            // Respuesta en caso de error
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error al procesar el archivo");
            response.put("success", false);

            return ResponseEntity.status(500).body(response);
        }
    }

    // Enviar correo al alumno
    @PostMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> sendEmail(@PathVariable Long id) {
        Alumno alumno = alumnoService.getAlumnoById(id);

        String message = String.format(
                "Información del alumno:\n\nNombre: %s %s\nMatrícula: %s\nPromedio: %.2f",
                alumno.getNombres(),
                alumno.getApellidos(),
                alumno.getMatricula(),
                alumno.getPromedio()
        );

        try {
            String topicArn = "arn:aws:sns:us-east-1:320004501035:alumnos-notifications";
            snsService.publishToTopic(topicArn, "Información del Alumno", message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notificación enviada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "No se pudo enviar la notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Iniciar sesión y crear sesión en DynamoDB
    @PostMapping("/{id}/session/login")
    public ResponseEntity<Map<String, Object>> login(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Alumno alumno = alumnoService.getAlumnoById(id);

        if (alumno == null || !alumno.getPassword().equals(request.get("password"))) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Credenciales incorrectas");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Generar un sessionString de 128 caracteres
        String sessionString = generateRandomString(128);

        // Crear la sesión en DynamoDB
        dynamoDbService.createSession(id, sessionString);

        // Respuesta detallada
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sesión creada con éxito");
        response.put("sessionString", sessionString);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }


    // Verificar sesión en DynamoDB
    @PostMapping("/{id}/session/verify")
    public ResponseEntity<Map<String, String>> verifySession(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String sessionString = request.get("sessionString");

        boolean isValid = dynamoDbService.verifySession(id, sessionString);

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Sesión válida"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Sesión inválida"));
        }
    }

    // Cerrar sesión en DynamoDB
    @PostMapping("/{id}/session/logout")
    public ResponseEntity<Map<String, String>> logout(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String sessionString = request.get("sessionString");
        dynamoDbService.logoutSession(id, sessionString);
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada"));
    }
}
