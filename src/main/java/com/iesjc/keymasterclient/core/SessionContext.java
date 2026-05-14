package com.iesjc.keymasterclient.core;

/**
 * Singleton que almacena la información global de la sesión del usuario.
 */
public class SessionContext {

    // La única instancia estática de la clase (Patrón Singleton)
    private static SessionContext instance;

    // --- CONFIGURACIÓN GLOBAL ---
    // Se podria leer de un archivo config.properties
    public static final String BASE_URL = "http://localhost:8080/api";

    // --- VARIABLES DE ESTADO ---
    private String token;
    private String username;
    private String rol;

    // Constructor privado para evitar que otras clases hagan "new SessionContext()"
    private SessionContext() {}

    // Method de acceso global
    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    // --- MÉTODOS DE NEGOCIO ---

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public void clear() {
        this.token = null;
        this.username = null;
        this.rol = null;
    }

    // --- GETTERS Y SETTERS ---

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}