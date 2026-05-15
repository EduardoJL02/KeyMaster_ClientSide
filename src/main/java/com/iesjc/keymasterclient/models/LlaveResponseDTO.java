package com.iesjc.keymasterclient.models;

import lombok.*;

/**
 * DTO que representa los datos de una Llave recibidos desde el servidor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LlaveResponseDTO {

    private Integer idLlave;
    private String codigoInterno;   // "LL-001" o "A3.2.2"
    private String estado;          // "DISPONIBLE", "EN_USO", "PERDIDA", "MANTENIMIENTO"

    // Datos aplanados del Espacio al que pertenece (para mostrar en la tabla)
    private Integer idEspacio;
    private String codigoEspacio;   // "Aula 204"
    private String tipoEspacio;         // "Aula"
    private String descripcionEspacio;  // "Aula de Informática Avanzada"
}
