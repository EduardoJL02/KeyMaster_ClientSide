package com.iesjc.keymasterclient.core;

/**
 * Enumeración que centraliza las rutas de los archivos FXML.
 */

// TODO: Si el proyecto crece mucho, podríamos dividir esta enumeración en varias (por ejemplo, una para cada módulo: AuthView, DashboardView, InventoryView, etc.)
public enum View {
    LOGIN("/fxml/LoginView.fxml"),
    DASHBOARD("/fxml/DashboardView.fxml"),
    INVENTARIO("/fxml/InventarioView.fxml"),
    STAFF("/fxml/PersonalView.fxml"),
    LOANS("/fxml/PrestamosView.fxml"),
    REPORTS("/fxml/ReportesView.fxml"),
    SETTINGS("/fxml/settings-view.fxml");

    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}