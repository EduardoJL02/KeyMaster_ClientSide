package com.iesjc.keymasterclient;

import com.iesjc.keymasterclient.core.Router;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class KeyMasterApplication extends Application {

    @Override
    public void start(Stage stage) {
        Router.inicializar(stage);

        // Configuracion del comportamiento al pulsar la 'X' de la ventana
        stage.setOnCloseRequest(event -> {
            // Consumimos el evento para evitar que la ventana se cierre automáticamente
            event.consume();
            mostrarConfirmacionSalida(stage);
        });

        // Mostrar ventana
        stage.show();

        // 4. (Opcional por ahora) Lanzaríamos la primera pantalla.
        // Descomentaremos esto cuando tengamos el LoginView.fxml creado:
         Router.irALogin();
    }

    /**
     * Muestra un cuadro de diálogo nativo preguntando si se desea salir.
     */
    private void mostrarConfirmacionSalida(Stage stage) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Salida");
        alerta.setHeaderText("¿Estás seguro de que deseas salir?");
        alerta.setContentText("Cualquier operación en curso podría cancelarse.");

        // Mostrar la alerta y esperar a que el usuario pulse un botón
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Si pulsa OK, cerramos la ventana y la aplicación se detiene
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}