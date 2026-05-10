package com.iesjc.keymasterclient.services;

import com.iesjc.keymasterclient.core.SessionManager;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final SessionManager sessionManager;

    public ApiService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Realiza una petición GET asíncrona.
     */
    public void getAsync(String endpoint, Consumer<String> onSuccess, Consumer<String> onError) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .header("Accept", "application/json");

        // Inyectar JWT si existe
        if (sessionManager.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + sessionManager.getToken());
        }

        client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> handleResponse(response, onSuccess, onError)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept("Error de conexión: " + ex.getMessage()));
                    return null;
                });
    }

    /**
     * Realiza una petición POST asíncrona con un cuerpo JSON.
     */
    public void postAsync(String endpoint, String jsonBody, Consumer<String> onSuccess, Consumer<String> onError) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (sessionManager.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + sessionManager.getToken());
        }

        client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> handleResponse(response, onSuccess, onError)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept("Error de conexión: " + ex.getMessage()));
                    return null;
                });
    }

    private void handleResponse(HttpResponse<String> response, Consumer<String> onSuccess, Consumer<String> onError) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            onSuccess.accept(response.body());
        } else if (response.statusCode() == 401) {
            // RSIP: Intercepción global de token caducado
            sessionManager.logout();
            onError.accept("Sesión expirada. Por favor, vuelva a iniciar sesión.");
            // Aquí notificaríamos al Router para volver al Login
        } else {
            onError.accept("Error del servidor HTTP: " + response.statusCode());
        }
    }

    // TODO: Implementar postAsync, putAsync, deleteAsync siguiendo el mismo patrón.
}
