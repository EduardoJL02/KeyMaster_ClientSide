package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.viewmodels.LoginViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    // Instanciamos el cerebro de esta pantalla
    private final LoginViewModel viewModel = new LoginViewModel();

    @FXML
    public void initialize() {
        // 1. Enlazamos los textos de la pantalla con las variables del ViewModel
        txtUsername.textProperty().bindBidirectional(viewModel.usernameProperty());
        txtPassword.textProperty().bindBidirectional(viewModel.passwordProperty());

        // 2. Enlazamos el mensaje de error para que aparezca solo
        lblError.textProperty().bind(viewModel.errorMessageProperty());

        // 3. LA MAGIA: Lógica de deshabilitación del botón (Cumpliendo la tabla de tu PDF)
        // El botón se desactiva SI:
        // - El usuario está vacío O
        // - La contraseña está vacía O
        // - Se está cargando la petición (isLoading)
        btnLogin.disableProperty().bind(
                txtUsername.textProperty().isEmpty()
                        .or(txtPassword.textProperty().isEmpty())
                        .or(viewModel.isLoadingProperty())
        );

        // 4. Cambiar el texto del botón si está cargando (Mejora visual de UX)
        viewModel.isLoadingProperty().addListener((observable, oldValue, isNowLoading) -> {
            if (isNowLoading) {
                btnLogin.setText("Conectando...");
            } else {
                btnLogin.setText("Iniciar sesión");
            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Al pulsar el botón (o dar a Enter en el PasswordField), avisamos al ViewModel
        viewModel.iniciarSesion();
    }
}