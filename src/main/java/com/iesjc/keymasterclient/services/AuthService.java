package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iesjc.keymasterclient.core.SessionManager;
import com.iesjc.keymasterclient.models.AuthResponse;
import com.iesjc.keymasterclient.models.LoginRequest;

import java.util.function.Consumer;

public class AuthService {

    private final ApiService apiService;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;

    public AuthService() {
        this.sessionManager = SessionManager.getInstance();
        this.apiService = new ApiService(this.sessionManager);
        this.objectMapper = new ObjectMapper();
    }

    public void login(String username, String password, Runnable onSuccess, Runnable onUnauthorized, Consumer<String> onError) {
        try {
            // 1. Crear el DTO y pasarlo a JSON
            LoginRequest request = new LoginRequest(username, password);
            String jsonBody = objectMapper.writeValueAsString(request);

            // 2. Hacer la petición POST al backend
            apiService.postAsync("/auth/login", jsonBody,
                    // Respuesta 200 OK
                    responseBody -> {
                        try {
                            // Parsear la respuesta y guardar el Token en el SessionManager
                            AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
                            sessionManager.setToken(authResponse.token());
                            onSuccess.run();
                        } catch (Exception e) {
                            onError.accept("Error al procesar la respuesta del servidor.");
                        }
                    },
                    // Manejo de Errores (401 u otros)
                    errorMessage -> {
                        if (errorMessage.contains("401")) {
                            onUnauthorized.run();
                        } else {
                            onError.accept(errorMessage);
                        }
                    }
            );
        } catch (Exception e) {
            onError.accept("Error interno al preparar la petición.");
        }
    }
}