package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iesjc.keymasterclient.models.InformeRequestDTO;
import com.iesjc.keymasterclient.models.InformeRowResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio encargado de las peticiones analíticas y de auditoría (Informes).
 * Reutiliza el HttpClient y el Token JWT de la clase base ApiService.
 */
public class InformeApiService extends ApiService {

    /**
     * 1. OBTENER VISTA PREVIA (JSON)
     * Envía los criterios de filtrado y recibe las filas que se pintarán en la tabla de JavaFX.
     * URL: POST http://localhost:8080/api/informes/vista-previa
     */
    public CompletableFuture<List<InformeRowResponseDTO>> obtenerVistaPrevia(InformeRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Convertimos el DTO con los filtros a cadena JSON
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                // Construimos la petición POST autenticada
                HttpRequest request = getAuthenticatedRequestBuilder("/informes/vista-previa")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // Enviamos la petición esperando una respuesta de texto plano (JSON)
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Mapeamos el JSON recibido a una lista de DTOs de fila
                    return objectMapper.readValue(response.body(), new TypeReference<List<InformeRowResponseDTO>>() {});
                } else {
                    throw new RuntimeException("Error al obtener la vista previa (HTTP " + response.statusCode() + "): " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al cargar la vista previa: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 2. DESCARGAR INFORME PDF (BINARY / BYTE ARRAY)
     * Envía los filtros y descarga el archivo PDF compilado por JasperReports en el Backend.
     * URL: POST http://localhost:8080/api/informes/pdf
     */
    public CompletableFuture<byte[]> descargarPdfInforme(InformeRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                HttpRequest request = getAuthenticatedRequestBuilder("/informes/pdf")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // Usamos ofByteArray() porque un PDF contiene caracteres binarios que se corromperían si se leen como texto.
                HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    // Devolvemos el array de bytes puro del archivo PDF
                    return response.body();
                } else {
                    // Si falla, el cuerpo de error lo leemos como string para saber qué pasó
                    String errorMsg = new String(response.body());
                    throw new RuntimeException("Error al generar el PDF (HTTP " + response.statusCode() + "): " + errorMsg);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al descargar el informe PDF: " + e.getMessage(), e);
            }
        });
    }
}