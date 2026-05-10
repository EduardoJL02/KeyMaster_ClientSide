package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.services.AuthService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final BooleanProperty isAuthenticating = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private final AuthService authService;

    public LoginViewModel() {
        this.authService = new AuthService();
    }

    public void authenticate(Runnable onSuccess, Runnable onUnauthorized, Runnable onError) {
        isAuthenticating.set(true);
        errorMessage.set("");

        // Llamada asíncrona real a Spring Boot
        authService.login(
                username.get(),
                password.get(),
                // Éxito: Guardamos el estado y ejecutamos la acción del controlador
                () -> {
                    isAuthenticating.set(false);
                    onSuccess.run();
                },
                // No autorizado: 401
                () -> {
                    isAuthenticating.set(false);
                    onUnauthorized.run();
                },
                // Error de red / Servidor
                (errorMsg) -> {
                    isAuthenticating.set(false);
                    errorMessage.set(errorMsg);
                    onError.run();
                }
        );
    }

    // Getters para bindings
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public BooleanProperty isAuthenticatingProperty() { return isAuthenticating; }
    public StringProperty errorMessageProperty() { return errorMessage; }
}