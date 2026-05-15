package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.viewmodels.NuevaLlaveViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class NuevaLlaveViewController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtEspacio;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final NuevaLlaveViewModel viewModel = new NuevaLlaveViewModel();

    @FXML
    public void initialize() {
        txtCodigo.textProperty().bindBidirectional(viewModel.codigoInternoProperty());
        txtEspacio.textProperty().bindBidirectional(viewModel.idEspacioProperty());
        lblError.textProperty().bind(viewModel.errorMessageProperty());

        // Deshabilitar botón si faltan datos o si está cargando
        btnGuardar.disableProperty().bind(
                txtCodigo.textProperty().isEmpty()
                        .or(txtEspacio.textProperty().isEmpty())
                        .or(viewModel.isLoadingProperty())
        );
    }

    // Method para inyectar el evento de recarga desde el Inventario
    public void setOnSuccessCallback(Consumer<LlaveResponseDTO> callback) {
        viewModel.setOnSuccessCallback(nuevaLlave -> {
            callback.accept(nuevaLlave);
            cerrarModal(); // Cerramos la ventana si tod0 fue bien
        });
    }

    @FXML
    private void handleGuardar() {
        viewModel.guardarLlave();
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}