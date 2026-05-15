package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iesjc.keymasterclient.core.SessionContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Servicio central para la comunicación HTTP asíncrona con el Backend Spring Boot.
 * (La autenticación se maneja exclusivamente en AuthService)
 */
public class ApiService {

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    public ApiService() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Construye una petición HTTP base inyectando automáticamente el Token JWT si existe.
     * Este métod0 será utilizado por el resto de servicios (LlaveService, PrestamoService, etc.)
     */
    protected HttpRequest.Builder getAuthenticatedRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(SessionContext.BASE_URL + endpoint))
                .header("Content-Type", "application/json");

        // Si tenemos sesión iniciada, inyectamos el Token de forma automática
        String token = SessionContext.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return builder;
    }
}