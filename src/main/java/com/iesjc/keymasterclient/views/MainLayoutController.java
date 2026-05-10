package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.core.Router;
import com.iesjc.keymasterclient.core.SessionManager;
import com.iesjc.keymasterclient.core.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane contentArea;
    @FXML private Label lblUserStatus;
    @FXML private ToggleButton btnDashboard, btnInventory, btnStaff, btnLoans, btnReports, btnSettings;

    @FXML
    public void initialize() {
        // Notificamos al Router cuál es el contenedor de las sub-vistas
        Router.getInstance().setContentArea(contentArea);

        // Binding del nombre de usuario
        lblUserStatus.textProperty().bind(
                SessionManager.getInstance().userNameProperty().map(name -> "Conectado: " + name)
        );

        // Cargar Dashboard por defecto
        Router.getInstance().switchView(View.DASHBOARD);
    }

    @FXML
    private void onNavigate(javafx.event.ActionEvent event) {
        ToggleButton btn = (ToggleButton) event.getSource();
        if (btn == btnDashboard) Router.getInstance().switchView(View.DASHBOARD);
        else if (btn == btnInventory) Router.getInstance().switchView(View.INVENTARIO);
        // ... añadir el resto de rutas ...
    }

    @FXML
    private void onLogout() {
        SessionManager.getInstance().logout();
        Router.getInstance().switchView(View.LOGIN);
    }
}