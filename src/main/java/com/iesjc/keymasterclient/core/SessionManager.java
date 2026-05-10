package com.iesjc.keymasterclient.core;

/**
 * Gestor global de sesión (Singleton).
 * Almacena el JWT y la información del usuario autenticado.
 */
public class SessionManager {

    private static SessionManager instance;
    private String token;

    // TODO: En el futuro aquí guardaremos el UsuarioDTO para saber quién está logueado y su ROL (Admin/Profesor)
    // private UsuarioDTO currentUser;

    // Constructor privado para evitar que se instancie desde fuera (Singleton)
    private SessionManager() {
    }

    // Métod0 para obtener la única instancia
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Limpia los datos de sesión cuando el usuario sale o el token caduca.
     */
    public void logout() {
        this.token = null;
        // this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return this.token != null && !this.token.trim().isEmpty();
    }
}