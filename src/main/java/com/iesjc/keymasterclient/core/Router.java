package com.iesjc.keymasterclient.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Router {
    private static Router instance;
    private Stage primaryStage;

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

    /**
     * Cambia la escena actual por la vista indicada.
     */
    public void switchView(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            Parent root = loader.load();

            // Si la escena no existe, la creamos. Si existe, solo cambiamos el root.
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1024, 768));
            } else {
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + view.getFxmlPath());
            e.printStackTrace();
        }
    }
}