package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iesjc.keymasterclient.models.LlaveRequestDTO;
import com.iesjc.keymasterclient.models.LlaveResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio encargado de las operaciones HTTP relacionadas con las Llaves.
 * Hereda de ApiService para aprovechar la inyección del Token y el HttpClient.
 */
public class LlaveApiService extends ApiService {

    /**
     * Obtiene la lista completa de llaves desde el servidor de forma asíncrona.
     */
    public CompletableFuture<List<LlaveResponseDTO>> obtenerTodasLasLlaves() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Construir la petición usando el method heredado (ya incluye el Bearer Token)
                HttpRequest request = getAuthenticatedRequestBuilder("/llaves")
                        .GET()
                        .build();

                // 2. Enviar la petición al servidor
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 3. Evaluar la respuesta
                if (response.statusCode() == 200) {
                    // Usar TypeReference para que Jackson sepa que es una Lista de DTOs y no un solo objeto
                    return objectMapper.readValue(response.body(), new TypeReference<List<LlaveResponseDTO>>() {});
                } else {
                    throw new RuntimeException("Error al obtener el inventario (HTTP " + response.statusCode() + "): " + response.body());
                }

            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al cargar llaves: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Envía una petición POST para crear una nueva llave.
     */
    public CompletableFuture<LlaveResponseDTO> crearLlave(LlaveRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                HttpRequest request = getAuthenticatedRequestBuilder("/llaves")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) { // 201 Created
                    return objectMapper.readValue(response.body(), LlaveResponseDTO.class);
                } else {
                    throw new RuntimeException("Error al crear la llave: " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
            }
        });
    }

    // Más adelante añadiremos aquí: actualizarLlave(), eliminarLlave()

    public CompletableFuture<LlaveResponseDTO> actualizarLlave(Integer idLlave, LlaveRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                HttpRequest request = getAuthenticatedRequestBuilder("/llaves/" + idLlave)
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), LlaveResponseDTO.class);
                } else {
                    throw new RuntimeException("Error al actualizar la llave: " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Void> eliminarLlave(Integer idLlave) {
        return CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = getAuthenticatedRequestBuilder("/llaves/" + idLlave)
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 204) { // 204 No Content es la respuesta esperada
                    throw new RuntimeException("Error al eliminar la llave: " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
            }
        });
    }
}