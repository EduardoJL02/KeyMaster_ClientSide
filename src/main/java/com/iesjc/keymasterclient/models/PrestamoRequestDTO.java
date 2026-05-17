package com.iesjc.keymasterclient.models;

import lombok.*;

/**
 * DTO para solicitar la creación de un nuevo préstamo al servidor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoRequestDTO {

    // Identificadores físicos y unas notas opcionales
    private Integer idLlave;
    private Integer idProfesor;
    private String observaciones;

}