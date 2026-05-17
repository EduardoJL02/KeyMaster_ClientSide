package com.iesjc.keymasterclient.models;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO que representa los datos completos de un Préstamo recibidos desde el servidor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoResponseDTO {

    private Integer idPrestamo;

    // Fechas de la transacción
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin; // Será null si el préstamo sigue activo
    private String observaciones;

    // Datos aplanados de la Llave
    private Integer idLlave;
    private String codigoLlave;    // "A3.2.2"
    private String estadoLlave;    // "EN_USO"

    // Datos aplanados del Espacio asociado a la llave
    private String codigoEspacio;  // "A3.2.2"

    // Datos aplanados del Profesor
    private Integer idProfesor;
    private String nombreProfesor; // "Mercedes"
    private String apellidosProfesor; // "Limón Echevarría"

    /**
     * Métod0 auxiliar útil para la tabla de JavaFX
     * Concatena nombre y apellidos para mostrarlo en una sola columna.
     */
    public String getNombreCompletoProfesor() {
        return (nombreProfesor != null ? nombreProfesor : "") + " " +
                (apellidosProfesor != null ? apellidosProfesor : "");
    }
}