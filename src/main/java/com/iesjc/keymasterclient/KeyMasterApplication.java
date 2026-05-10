package com.iesjc.keymasterclient;

import com.iesjc.keymasterclient.core.Router;
import com.iesjc.keymasterclient.core.View;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class KeyMasterApplication extends Application {
    @Override
    public void start(Stage stage) {
        Router router = Router.getInstance();
        router.init(stage);

        stage.setTitle("KeyMaster Center - Inicio de sesión"); //
        stage.setResizable(false); //

        // Interceptar el cierre de la ventana
        stage.setOnCloseRequest(event -> {
            event.consume(); // Detener el cierre automático
            mostrarConfirmacionSalida(stage);
        });

        router.switchView(View.LOGIN);
    }

    private void mostrarConfirmacionSalida(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText("¿Estás seguro de que deseas salir?");
        alert.setContentText("Se cerrará la aplicación KeyMaster Center.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            stage.close(); //
        }
    }

    public static void main(String[] args) {
        launch();
    }
}