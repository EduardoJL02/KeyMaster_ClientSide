package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iesjc.keymasterclient.models.PrestamoRequestDTO;
import com.iesjc.keymasterclient.models.PrestamoResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio encargado de las operaciones HTTP relacionadas con los Préstamos.
 */
public class PrestamoApiService extends ApiService {

    /**
     * 1. OBTENER PRÉSTAMOS ACTIVOS
     * Trae solo los préstamos que aún no han sido devueltos (horaFin == null).
     */
    public CompletableFuture<List<PrestamoResponseDTO>> obtenerPrestamosActivos() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = getAuthenticatedRequestBuilder("/prestamos/activos")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<PrestamoResponseDTO>>() {});
                } else {
                    throw new RuntimeException("Error al cargar préstamos activos (HTTP " + response.statusCode() + "): " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al cargar préstamos: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 2. CREAR UN NUEVO PRÉSTAMO (ENTREGAR LLAVE)
     * Envía los IDs de la llave y del profesor para registrar la transacción.
     */
    public CompletableFuture<PrestamoResponseDTO> crearPrestamo(PrestamoRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                HttpRequest request = getAuthenticatedRequestBuilder("/prestamos")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) { // 201 Created
                    return objectMapper.readValue(response.body(), PrestamoResponseDTO.class);
                } else {
                    throw new RuntimeException("Error al registrar el préstamo: " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al crear préstamo: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 3. FINALIZAR PRÉSTAMO (DEVOLVER LLAVE)
     * Avisa al servidor de que la llave ha sido devuelta para que registre la hora de fin
     * y vuelva a poner el estado de la llave en DISPONIBLE.
     */
    public CompletableFuture<PrestamoResponseDTO> finalizarPrestamo(Integer idPrestamo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Usamos PUT con un cuerpo vacío porque solo queremos actualizar el estado del préstamo sin enviar datos adicionales.
                HttpRequest request = getAuthenticatedRequestBuilder("/prestamos/" + idPrestamo + "/devolver")
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), PrestamoResponseDTO.class);
                } else {
                    throw new RuntimeException("Error al finalizar el préstamo (HTTP " + response.statusCode() + "): " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al devolver la llave: " + e.getMessage(), e);
            }
        });
    }
}