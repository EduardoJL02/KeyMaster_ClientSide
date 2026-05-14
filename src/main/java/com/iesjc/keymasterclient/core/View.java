package com.iesjc.keymasterclient.core;

/**
 * Enumeración que centraliza las rutas de los archivos FXML.
 */
public enum View {
    LOGIN("/fxml/LoginView.fxml"),
    DASHBOARD("/fxml/dashboard-view.fxml"),
    INVENTARIO("/fxml/inventory-view.fxml"),
    MAIN_LAYOUT("/fxml/main-layout.fxml"),
    STAFF("/fxml/staff-view.fxml"),
    LOANS("/fxml/loans-view.fxml"),
    REPORTS("/fxml/reports-view.fxml"),
    SETTINGS("/fxml/settings-view.fxml");

    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}