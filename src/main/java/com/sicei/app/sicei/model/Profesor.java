package com.sicei.app.sicei.model;

import lombok.Data;

@Data
public class Profesor {
    private Long id;
    private String numeroEmpleado;
    private String nombres;
    private String apellidos;
    private Integer horasClase;
}
