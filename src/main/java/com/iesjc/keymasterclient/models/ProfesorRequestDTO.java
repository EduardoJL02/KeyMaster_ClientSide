package com.iesjc.keymasterclient.models;

import lombok.*;

/**
 * DTO para enviar datos al servidor al crear o editar un Profesor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorRequestDTO {

    private String dni;
    private String nombre;
    private String apellidos;
    private String email;

    // Al igual que con el Espacio, solo enviamos el ID numérico del departamento
    private Integer idDepartamento;
}