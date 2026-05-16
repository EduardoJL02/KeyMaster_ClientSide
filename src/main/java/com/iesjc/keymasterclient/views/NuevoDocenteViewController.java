package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.viewmodels.NuevoDocenteViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class NuevoDocenteViewController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDepartamento;

    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final NuevoDocenteViewModel viewModel = new NuevoDocenteViewModel();

    @FXML
    public void initialize() {
        // Vinculaciones bidireccionales
        txtDni.textProperty().bindBidirectional(viewModel.dniProperty());
        txtNombre.textProperty().bindBidirectional(viewModel.nombreProperty());
        txtApellidos.textProperty().bindBidirectional(viewModel.apellidosProperty());
        txtEmail.textProperty().bindBidirectional(viewModel.emailProperty());
        txtDepartamento.textProperty().bindBidirectional(viewModel.idDepartamentoProperty());

        lblError.textProperty().bind(viewModel.errorMessageProperty());

        // Deshabilitar el botón si está procesando la petición
        btnGuardar.disableProperty().bind(viewModel.isLoadingProperty());
    }

    public void setOnSuccessCallback(Consumer<ProfesorResponseDTO> callback) {
        viewModel.setOnSuccessCallback(nuevoProfesor -> {
            callback.accept(nuevoProfesor);
            cerrarModal(); // Si el guardado es exitoso, destruimos la ventana
        });
    }

    @FXML
    private void handleGuardar() {
        viewModel.guardarDocente();
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}