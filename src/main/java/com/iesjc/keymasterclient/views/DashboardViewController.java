package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.viewmodels.DashboardViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardViewController {

    @FXML private Label lblWelcome;

    private final DashboardViewModel viewModel = new DashboardViewModel();

    @FXML
    public void initialize() {
        // Enlazamos el mensaje de bienvenida
        lblWelcome.textProperty().bind(viewModel.welcomeMessageProperty());
    }
}