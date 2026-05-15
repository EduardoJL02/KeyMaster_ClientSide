package com.iesjc.keymasterclient.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iesjc.keymasterclient.core.SessionContext;
import com.iesjc.keymasterclient.models.LoginRequestDTO;
import com.iesjc.keymasterclient.models.LoginResponseDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio exclusivo para la gestión de Autenticación (Login / Logout).
 */
public class AuthService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthService() {
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Realiza la petición de inicio de sesión de forma asíncrona.
     * @return CompletableFuture con 'true' si el login fue exitoso, o lanza una excepción si falla.
     */
    public CompletableFuture<Boolean> login(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Preparar el DTO con las credenciales
                LoginRequestDTO requestDto = new LoginRequestDTO(username, password);
                String jsonBody = objectMapper.writeValueAsString(requestDto);

                // 2. Construir la petición POST
                // Fíjate que aquí no usamos Token en la cabecera, porque aún no estamos logueados
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SessionContext.BASE_URL + "/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // 3. Enviar petición y esperar respuesta
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 4. Analizar el resultado
                if (response.statusCode() == 200) {
                    LoginResponseDTO loginResponse = objectMapper.readValue(response.body(), LoginResponseDTO.class);

                    // 5. Guardamos la sesión globalmente para que toda la app sepa quién somos
                    SessionContext session = SessionContext.getInstance();
                    session.setToken(loginResponse.getToken());
                    session.setUsername(loginResponse.getUsername());
                    session.setRol(loginResponse.getRol());

                    return true;
                } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                    throw new RuntimeException("Usuario o contraseña incorrectos.");
                } else {
                    // Si el servidor devuelve 401 Unauthorized
                    throw new RuntimeException("Error interno del servidor (HTTP " + response.statusCode() + "): " + response.body());
                }

            } catch (Exception e) {
                // Capturamos cualquier error de red (Ej. El backend de Spring Boot está apagado)
                throw new RuntimeException("Error al conectar con el servidor: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Cierra la sesión actual del usuario.
     */
    public void logout() {
        // Borramos los datos de la memoria
        SessionContext.getInstance().clear();

        // El Router se encargará de llevarnos a la pantalla de Login después de llamar a este method.
    }
}