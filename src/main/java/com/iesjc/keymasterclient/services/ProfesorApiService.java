package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iesjc.keymasterclient.models.ProfesorRequestDTO;
import com.iesjc.keymasterclient.models.ProfesorResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio encargado de las operaciones HTTP relacionadas con los Profesores.
 * Hereda de ApiService para reutilizar el cliente, el mapeador y la seguridad JWT.
 */
public class ProfesorApiService extends ApiService {

    /**
     * Obtiene la lista completa de profesores desde el servidor de forma asíncrona.
     * (Equivalente al GET http://localhost:8080/api/profesores del backend)
     */
    public CompletableFuture<List<ProfesorResponseDTO>> obtenerTodosLosProfesores() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Construimos la petición inyectando el token automáticamente hacia el endpoint /profesores
                HttpRequest request = getAuthenticatedRequestBuilder("/profesores")
                        .GET()
                        .build();

                // 2. Enviamos la petición de forma asíncrona mediante el HttpClient heredado
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 3. Evaluamos el código de estado HTTP
                if (response.statusCode() == 200) {
                    // Usamos TypeReference para que Jackson entienda que es un Array JSON de profesores
                    return objectMapper.readValue(response.body(), new TypeReference<List<ProfesorResponseDTO>>() {});
                } else {
                    throw new RuntimeException("Error al obtener el personal (HTTP " + response.statusCode() + "): " + response.body());
                }

            } catch (Exception e) {
                // Captura fallos de red o errores de parseo controladamente
                throw new RuntimeException("Error de conexión al cargar el personal docente: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Envía una petición POST para registrar un nuevo docente en el sistema.
     */
    public CompletableFuture<ProfesorResponseDTO> crearProfesor(ProfesorRequestDTO requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Convertimos nuestro DTO de petición a formato JSON
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                // 2. Construimos la petición POST
                HttpRequest request = getAuthenticatedRequestBuilder("/profesores")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // 3. Enviamos la petición
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 4. Evaluamos la respuesta (Esperamos un 201 Created)
                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), ProfesorResponseDTO.class);
                } else {
                    throw new RuntimeException("Error al registrar el docente: " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error de conexión al crear profesor: " + e.getMessage(), e);
            }
        });
    }

    // TODO: Aquí añadiremos en las próximas iteraciones: crearProfesor(), darDeBajaProfesor(), etc.
}