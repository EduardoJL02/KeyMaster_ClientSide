package com.iesjc.keymasterclient.core;

/**
 * Enumeración que centraliza las rutas de los archivos FXML.
 */
public enum View {
    LOGIN("/fxml/login-view.fxml"),
    DASHBOARD("/fxml/dashboard-view.fxml"),
    INVENTARIO("/fxml/inventory-view.fxml");

    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}