package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.core.Router;
import com.iesjc.keymasterclient.core.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private StackPane pnlContent;
    @FXML private Label lblUserInfo;
    @FXML private Label lblViewTitle;
    @FXML private Button btnSettings;

    @FXML
    public void initialize() {
        // 1. Registramos este contenedor en el Router para poder navegar
        Router.setContentContainer(pnlContent);

        // 2. Cargamos info del usuario desde el Singleton
        SessionContext session = SessionContext.getInstance();
        lblUserInfo.setText("Bienvenido, " + session.getUsername() + " (" + session.getRol() + ")");

        // 3. SEGURIDAD (RBAC): Ocultar ajustes si no es Jefatura
        if (!"JEFATURA".equals(session.getRol())) {
            btnSettings.setVisible(false);
            btnSettings.setManaged(false); // No ocupa espacio al estar oculto
        }

        // 4. Cargamos el Dashboard por defecto al entrar
        Router.irADashboard();
    }

    @FXML private void irADashboard() { lblViewTitle.setText("Dashboard"); Router.irADashboard(); }
    @FXML private void irAInventario() { lblViewTitle.setText("Inventario de Llaves");  Router.irAInventario();  }
    @FXML private void irAPersonas() { lblViewTitle.setText("Gestión de Personal"); Router.irAPersonas();}
    @FXML private void irAPrestamos() { lblViewTitle.setText("Préstamos Activos"); Router.irAPrestamos();}
    @FXML private void irAReportes() { lblViewTitle.setText("Informes y Auditoría del Centro"); Router.irAReportes(); }
    @FXML private void irASettings() { lblViewTitle.setText("Configuración Global"); }

    @FXML
    private void handleLogout() {
        SessionContext.getInstance().clear();
        Router.irALogin();
    }
}