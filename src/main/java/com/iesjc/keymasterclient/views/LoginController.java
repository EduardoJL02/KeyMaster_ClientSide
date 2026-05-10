package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.core.Router;
import com.iesjc.keymasterclient.core.View;
import com.iesjc.keymasterclient.viewmodels.LoginViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private LoginViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new LoginViewModel();

        // 1. Binding bidireccional de los campos de texto
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());

        // 2. Validación en tiempo real: El botón se habilita si hay texto y no está cargando
        loginButton.disableProperty().bind(
                Bindings.isEmpty(usernameField.textProperty())
                        .or(Bindings.isEmpty(passwordField.textProperty()))
                        .or(viewModel.isAuthenticatingProperty())
        );

        // 3. Deshabilitar campos y mostrar loading mientras se autentica
        usernameField.disableProperty().bind(viewModel.isAuthenticatingProperty());
        passwordField.disableProperty().bind(viewModel.isAuthenticatingProperty());
        loadingIndicator.visibleProperty().bind(viewModel.isAuthenticatingProperty());
    }

    @FXML
    private void onLoginClick() {
        // El botón ya se ha desactivado por el binding. Llamamos al ViewModel
        viewModel.authenticate(
                // onSuccess (200 OK)
                () -> Router.getInstance().switchView(View.DASHBOARD),

                // onUnauthorized (401)
                () -> {
                    viewModel.errorMessageProperty().set("Usuario o contraseña incorrectos");
                    passwordField.clear(); // Vaciar contraseña
                    usernameField.requestFocus(); // Devolver el foco al usuario
                },

                // onError (5xx)
                () -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error de conexión");
                    alert.setHeaderText(null);
                    alert.setContentText("Error de conexión con el servidor.");
                    alert.showAndWait();
                }
        );
    }
}