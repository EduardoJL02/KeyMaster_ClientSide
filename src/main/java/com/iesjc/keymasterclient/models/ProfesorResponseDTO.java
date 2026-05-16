package com.iesjc.keymasterclient.models;

import lombok.*;

/**
 * DTO que representa los datos de un Profesor/Docente recibidos desde el servidor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorResponseDTO {

    private Integer idProfesor;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private Boolean activo; // Para identificar si está dado de baja (Soft Delete)

    // Datos aplanados del Departamento al que pertenece
    private Integer idDepartamento;
    private String nombreDepartamento; // "Informática y Comunicaciones"
}