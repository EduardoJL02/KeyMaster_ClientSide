//package com.iesjc.keymasterclient.views;
//
//import com.iesjc.keymasterclient.core.Router;
//import com.iesjc.keymasterclient.core.View;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.control.ToggleButton;
//import javafx.scene.layout.StackPane;
//
//public class MainLayoutController {
//
//    @FXML private StackPane contentArea;
//    @FXML private Label lblUserStatus;
//    @FXML private ToggleButton btnDashboard, btnInventory, btnStaff, btnLoans, btnReports, btnSettings;
//
//    @FXML
//    public void initialize() {
//        // Notificamos al Router cuál es el contenedor de las sub-vistas
//        Router.getInstance().setContentArea(contentArea);
//
//        // Binding del nombre de usuario
//        lblUserStatus.textProperty().bind(
//                SessionManager.getInstance().userNameProperty().map(name -> "Conectado: " + name)
//        );
//
//        // Cargar Dashboard por defecto
//        Router.getInstance().switchView(View.DASHBOARD);
//    }
//
//    @FXML
//    private void onNavigate(javafx.event.ActionEvent event) {
//        ToggleButton selected = (ToggleButton) event.getSource();
//        if (selected == btnDashboard) Router.getInstance().switchView(View.DASHBOARD);
//        else if (selected == btnInventory) Router.getInstance().switchView(View.INVENTARIO);
//        else if (selected == btnStaff) Router.getInstance().switchView(View.STAFF);
//        else if (selected == btnLoans) Router.getInstance().switchView(View.LOANS);
//        else if (selected == btnReports) Router.getInstance().switchView(View.REPORTS);
//        else if (selected == btnSettings) Router.getInstance().switchView(View.SETTINGS);
//    }
//
//    @FXML
//    private void onLogout() {
//        SessionManager.getInstance().logout();
//        Router.getInstance().switchView(View.LOGIN);
//    }
//}