package com.iesjc.keymasterclient.core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Gestor global de sesión (Singleton).
 * Almacena el JWT y la información del usuario autenticado.
 */
public class SessionManager {

    private static SessionManager instance;
    private String token;

    private final StringProperty userName = new SimpleStringProperty("Usuario no identificado");

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

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String name) {
        this.userName.set(name);
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    /**
     * Limpia los datos de sesión cuando el usuario sale o el token caduca.
     */
    public void logout() {
        this.token = null;
        this.userName.set("Usuario no identificado");
        // this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return this.token != null && !this.token.trim().isEmpty();
    }
}