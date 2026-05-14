package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.core.Router;
import com.iesjc.keymasterclient.services.AuthService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel para la pantalla de inicio de sesión.
 * Conecta la interfaz gráfica (JavaFX) con la lógica de negocio (AuthService).
 */
public class LoginViewModel {

    // --- DEPENDENCIAS ---
    private final AuthService authService;

    // --- PROPIEDADES OBSERVABLES (Data Binding) ---
    // Estas variables se "atarán" mágicamente a los TextField y Labels de JavaFX
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public LoginViewModel() {
        this.authService = new AuthService();
    }

    // --- LÓGICA DE NEGOCIO ---

    public void iniciarSesion() {
        // 1. Validación básica en el frontend
        if (username.get().isBlank() || password.get().isBlank()) {
            errorMessage.set("Por favor, introduzca usuario y contraseña.");
            return;
        }

        // 2. Cambiamos el estado a "Cargando" (Esto desactivará el botón en la interfaz)
        isLoading.set(true);
        errorMessage.set(""); // Limpiamos errores anteriores

        // 3. Llamamos a nuestro servicio de red (Esto ocurre en un hilo secundario)
        authService.login(username.get(), password.get())
                .thenAccept(success -> {
                    // ¡ÉXITO! Hemos recibido el Token.
                    // Platform.runLater obliga a que el cambio de pantalla se haga en el Hilo Principal de JavaFX
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        System.out.println("Login exitoso. Navegando al Dashboard...");
                        Router.irADashboard(); // Redirigimos a la pantalla principal
                    });
                })
                .exceptionally(ex -> {
                    // ¡ERROR! (Contraseña incorrecta, servidor apagado, etc.)
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        // Extraemos el mensaje de error de la excepción (evitando textos técnicos largos)
                        String mensajeError = ex.getCause() != null ? ex.getCause().getMessage() : "Error de conexión con el servidor.";
                        errorMessage.set(mensajeError);
                    });
                    return null;
                });
    }

    // --- GETTERS PARA EL DATA BINDING ---
    // JavaFX necesita estos métodos para poder vincular la vista con el modelo

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }
}