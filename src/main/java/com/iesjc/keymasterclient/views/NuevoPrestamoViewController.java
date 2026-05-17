package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.models.PrestamoResponseDTO;
import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.viewmodels.NuevoPrestamoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.function.Consumer;

/**
 * Controlador para la ventana modal de registro de un nuevo préstamo.
 * Gestiona el mapeo visual de objetos complejos mediante StringConverters.
 */
public class NuevoPrestamoViewController {

    // --- ELEMENTOS ENLAZADOS AL FXML ---
    @FXML private ComboBox<ProfesorResponseDTO> cmbProfesor;
    @FXML private ComboBox<LlaveResponseDTO> cmbLlave;
    @FXML private TextArea txtObservaciones;

    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Instanciamos el cerebro orquestador de la transacción
    private final NuevoPrestamoViewModel viewModel = new NuevoPrestamoViewModel();

    /**
     * Métod0 de inicialización nativo de JavaFX.
     */
    @FXML
    public void initialize() {
        // 1. ASIGNACIÓN DE LAS LISTAS OBSERVABLES A LOS COMBOBOX
        cmbLlave.setItems(viewModel.getLlavesDisponibles());
        cmbProfesor.setItems(viewModel.getProfesoresActivos());

        // 2. ENLACES DE PROPIEDADES (Data Binding)
        cmbLlave.valueProperty().bindBidirectional(viewModel.llaveSeleccionadaProperty());
        cmbProfesor.valueProperty().bindBidirectional(viewModel.profesorSeleccionadoProperty());
        txtObservaciones.textProperty().bindBidirectional(viewModel.observacionesProperty());

        lblError.textProperty().bind(viewModel.errorMessageProperty());

        // 3. MEJORA DE ERGONOMÍA (StringConverters para renderizar objetos complejos)
        configurarConversoresVisuales();

        // 4. CONTROL DE ESTADO DE ACCIONES (Deshabilitar botones si está cargando)
        btnGuardar.disableProperty().bind(
                cmbLlave.valueProperty().isNull()
                        .or(cmbProfesor.valueProperty().isNull())
                        .or(viewModel.isLoadingProperty())
        );

        // 5. DISPARO INICIAL ASÍNCRONO CONCURRENTE
        // Descarga simultáneamente los catálogos de llaves y profesores al abrir la ventana
        viewModel.cargarDatosIniciales();
    }

    /**
     * Define las reglas para transformar los DTOs de profesores y llaves
     * en hilos de texto comprensibles para el operario en los desplegables.
     */
    private void configurarConversoresVisuales() {
        // Formato para el desplegable de Profesores
        cmbProfesor.setConverter(new StringConverter<>() {
            @Override
            public String toString(ProfesorResponseDTO profesor) {
                if (profesor == null) return "";
                // Muestra: "Mercedes Limón Echevarría (Informática y Comunicaciones)"
                return profesor.getNombre() + " " + profesor.getApellidos() +
                        " (" + (profesor.getNombreDepartamento() != null ? profesor.getNombreDepartamento() : "Sin Dept.") + ")";
            }

            @Override
            public ProfesorResponseDTO fromString(String string) {
                return null; // No se requiere conversión inversa al ser un ComboBox no editable
            }
        });

        // Formato para el desplegable de Llaves
        cmbLlave.setConverter(new StringConverter<>() {
            @Override
            public String toString(LlaveResponseDTO llave) {
                if (llave == null) return "";
                // Muestra: "Llave A3.2.2 [Aula Principal de 2º DAM]"
                return "Llave " + llave.getCodigoInterno() +
                        " [" + (llave.getDescripcionEspacio() != null ? llave.getDescripcionEspacio() : "Espacio General") + "]";
            }

            @Override
            public LlaveResponseDTO fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Permite inyectar el callback desde la ventana principal para refrescar la tabla.
     */
    public void setOnSuccessCallback(Consumer<PrestamoResponseDTO> callback) {
        viewModel.setOnSuccessCallback(nuevoPrestamo -> {
            callback.accept(nuevoPrestamo);
            cerrarModal(); // Si el guardado es exitoso en BD, destruimos la ventana flotante
        });
    }

    /**
     * Evento gatillo para el botón de confirmación verde "CONCEDER Y REGISTRAR".
     */
    @FXML
    private void handleGuardar() {
        viewModel.guardarPrestamo();
    }

    /**
     * Cierra de forma limpia el contenedor Stage actual.
     */
    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}