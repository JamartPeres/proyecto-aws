package com.sicei.app.sicei.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(nullable = false)
    private Double promedio;

    @Column
    private String fotoPerfilUrl; // Para almacenar la URL de S3

    @Column
    private String password;
}
