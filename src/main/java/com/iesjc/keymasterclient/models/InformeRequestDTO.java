package com.iesjc.keymasterclient.models;

import lombok.*;
import java.time.LocalDate;

/**
 * DTO que recopila los filtros de la UI para solicitar la generación de un informe.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformeRequestDTO {

    // "AUDITORIA_PRESTAMOS" o "ESTADO_LLAVES"
    private String tipoInforme;

    // "HOY", "SEMANA", "MES", "TRIMESTRE", "PERSONALIZADO"
    private String rangoRapido;

    // Campos acotados. Serán nulos si el rango rápido no es "PERSONALIZADO"
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}