package com.iesjc.keymasterclient.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestor centralizado de navegación (SceneManager).
 * Se encarga de cambiar el contenido de la ventana principal sin abrir ventanas nuevas.
 */
public class Router {

    // Referencia global a la ventana principal de la aplicación
    private static Stage ventanaPrincipal;

    /**
     * Se llama una sola vez al arrancar la app para guardar la referencia de la ventana.
     */
    public static void inicializar(Stage stage) {
        ventanaPrincipal = stage;
    }

    /**
     * Métod0 genérico para cambiar de pantalla.
     * @param rutaFxml La ruta del archivo FXML (ej. "/fxml/LoginView.fxml")
     * @param titulo El título que aparecerá en la barra superior de la ventana
     */
    public static void navegar(String rutaFxml, String titulo) {
        if (ventanaPrincipal == null) {
            throw new IllegalStateException("El Router no ha sido inicializado.");
        }

        try {
            // Cargamos el diseño FXML desde la carpeta resources
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(rutaFxml));
            Parent vista = loader.load();

            // Creamos una nueva "Escena" con ese diseño y la ponemos en la ventana
            Scene escena = new Scene(vista);
            ventanaPrincipal.setScene(escena);
            ventanaPrincipal.setTitle("KeyMaster San Juan de la Cruz - " + titulo);
            ventanaPrincipal.centerOnScreen(); // Centramos la ventana en el monitor

        } catch (IOException e) {
            System.err.println("Error crítico al cargar la vista: " + rutaFxml);
            e.printStackTrace();
            // Aquí en el futuro usaremos AlertHelper para mostrar un pop-up de error visual
        }
    }

    // Metodos de acceso rapido para las pantallas

    public static void irALogin() {
        navegar("/fxml/LoginView.fxml", "Iniciar Sesión");
    }

    public static void irADashboard() {
        navegar("/fxml/DashboardView.fxml", "Panel Principal");
    }
}