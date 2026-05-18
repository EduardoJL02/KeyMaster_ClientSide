package com.iesjc.keymasterclient.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class Router {

    private static Stage ventanaPrincipal;
    private static Pane pnlContent; // Referencia al centro del MainLayout

    public static void inicializar(Stage stage) {
        ventanaPrincipal = stage;
        ventanaPrincipal.setResizable(false);
    }

    // Para que el MainLayoutController nos pase su StackPane central
    public static void setContentContainer(Pane pane) {
        pnlContent = pane;
    }

    /**
     * Cambia la Escena completa (usado para Login o para cargar el Layout principal)
     */
    public static void cargarLayoutBase(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(rutaFxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1024.0, 768.0);
            ventanaPrincipal.setScene(scene);
            ventanaPrincipal.centerOnScreen();
            ventanaPrincipal.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cambia solo el CONTENIDO CENTRAL dentro del MainLayout
     */
    public static void navegarACentral(String rutaFxml) {
        if (pnlContent == null) {
            // Si el contenedor no está listo, cargamos primero el MainLayout
            cargarLayoutBase("/fxml/MainLayoutView.fxml");
        }

        try {
            FXMLLoader loader = new FXMLLoader(Router.class.getResource(rutaFxml));
            Parent vistaInterior = loader.load();

            // Limpiamos lo que hubiera y ponemos la nueva vista
            pnlContent.getChildren().clear();
            pnlContent.getChildren().add(vistaInterior);

        } catch (IOException e) {
            System.err.println("Error cargando vista interna: " + rutaFxml);
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE ACCESO RÁPIDO ---
    public static void irALogin() { cargarLayoutBase("/fxml/LoginView.fxml"); }
    public static void irAMainLayout() { cargarLayoutBase("/fxml/MainLayoutView.fxml"); }
    public static void irADashboard() { navegarACentral("/fxml/DashboardView.fxml"); }
    public static void irAInventario() { navegarACentral("/fxml/InventarioView.fxml");   }
    public static void irAPersonas() { navegarACentral("/fxml/PersonalView.fxml");    }
    public static void irAPrestamos() { navegarACentral("/fxml/PrestamosView.fxml");   }
    public static void irAReportes() { navegarACentral("/fxml/ReportesView.fxml");   }
}