package com.iesjc.keymasterclient.models;

import lombok.*;

/**
 * DTO para enviar datos al servidor al crear o editar una Llave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LlaveRequestDTO {
    private String codigoInterno;
    private Integer idEspacio; // El backend solo necesita saber el ID del espacio físico

}