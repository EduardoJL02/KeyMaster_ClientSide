package com.iesjc.keymasterclient.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Router {
    private static Router instance;
    private Stage primaryStage;
    private StackPane contentArea;

    private Router() {}

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    public void setContentArea(StackPane area) { this.contentArea = area; }

    /**
     * Cambia la escena actual por la vista indicada.
     */
    public void switchView(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            Parent root = loader.load();

            // Lógica de Enrutamiento Inteligente
            if (view == View.LOGIN || view == View.MAIN_LAYOUT) {
                primaryStage.getScene().setRoot(root);
                this.contentArea = null; // Resetear área interna
            } else if (contentArea != null) {
                // Es una vista interna (Dashboard, Inventario, etc.)
                contentArea.getChildren().setAll(root);
            } else {
                // Si intentamos cargar Dashboard sin MainLayout, cargamos MainLayout primero
                switchView(View.MAIN_LAYOUT);
                // La inicialización del MainLayoutController volverá a llamar a switchView(DASHBOARD)
            }

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + view.getFxmlPath());
            e.printStackTrace();
        }
    }
}