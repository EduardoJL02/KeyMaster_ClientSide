package com.iesjc.keymasterclient.models;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO que representa una fila de actividad en la tabla de vista previa de informes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformeRowResponseDTO {

    private Integer idRegistro;

    // Cuándo ocurrió el movimiento (Préstamo o Devolución)
    private LocalDateTime fechaHora;

    // "PRÉSTAMO" o "DEVOLUCIÓN"
    private String tipoAccion;

    // Datos descriptivos rápidos para el ojo del usuario
    private String codigoLlave;
    private String nombreCompletoDocente;

    // Auditoría: Nombre de usuario del conserje que estaba logueado
    private String usuarioConserje;

    private String observaciones;
}