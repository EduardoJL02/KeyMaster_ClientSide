package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.InformeRowResponseDTO;
import com.iesjc.keymasterclient.viewmodels.ReportesViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador visual de la interfaz de Reportes y Auditoría.
 * Gestiona las interacciones con los selectores de fechas, la carga de datos en la tabla
 * y la invocación de los cuadros de diálogo del Sistema Operativo para guardar archivos.
 */
public class ReportesViewController {

    // --- COMPONENTES ENLAZADOS POR FXML ---
    @FXML private ComboBox<String> cmbTipoInforme;
    @FXML private ComboBox<String> cmbRangoRapido;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;

    @FXML private Button btnVistaPrevia;
    @FXML private Button btnExportarPdf;
    @FXML private Button btnExportarExcel;

    @FXML private TableView<InformeRowResponseDTO> tablaPreview;
    @FXML private TableColumn<InformeRowResponseDTO, Integer> colId;
    @FXML private TableColumn<InformeRowResponseDTO, LocalDateTime> colFecha;
    @FXML private TableColumn<InformeRowResponseDTO, String> colAccion;
    @FXML private TableColumn<InformeRowResponseDTO, String> colLlave;
    @FXML private TableColumn<InformeRowResponseDTO, String> colDocente;
    @FXML private TableColumn<InformeRowResponseDTO, String> colConserje;
    @FXML private TableColumn<InformeRowResponseDTO, String> colObservaciones;

    @FXML private Label lblSuccess;
    @FXML private Label lblError;
    @FXML private Label lblCargando;

    // Instanciamos el cerebro de la pantalla
    private final ReportesViewModel viewModel = new ReportesViewModel();

    /**
     * Inicializador nativo de JavaFX ejecutado al cargar el FXML.
     */
    @FXML
    public void initialize() {
        // 1. DATA BINDING DE LOS FILTROS Y CONTROLES SUPERIORES
        cmbTipoInforme.valueProperty().bindBidirectional(viewModel.tipoInformeProperty());
        cmbRangoRapido.valueProperty().bindBidirectional(viewModel.rangoRapidoProperty());
        dpFechaInicio.valueProperty().bindBidirectional(viewModel.fechaInicioProperty());
        dpFechaFin.valueProperty().bindBidirectional(viewModel.fechaFinProperty());

        // 2. BINDING CONDICIONAL: Habilitar calendarios SOLO si el modo es "PERSONALIZADO"
        // Invertimos la propiedad booleana usando .not() para aplicar la lógica de inhabilitación (disable)
        dpFechaInicio.disableProperty().bind(viewModel.isPersonalizadoProperty().not());
        dpFechaFin.disableProperty().bind(viewModel.isPersonalizadoProperty().not());

        // 3. DATA BINDING DE REACCIONES INFRAESTRUCTURALES (MENSAJES Y SPINNER)
        lblSuccess.textProperty().bind(viewModel.successMessageProperty());
        lblError.textProperty().bind(viewModel.errorMessageProperty());
        lblCargando.visibleProperty().bind(viewModel.isLoadingProperty());

        // Deshabilitar los botones de acción si el sistema se encuentra procesando una petición HTTP
        btnVistaPrevia.disableProperty().bind(viewModel.isLoadingProperty());
        btnExportarPdf.disableProperty().bind(viewModel.isLoadingProperty());

        // 4. CONFIGURACIÓN DE LAS COLUMNAS DE LA TABLA DE VISTA PREVIA
        colId.setCellValueFactory(new PropertyValueFactory<>("idRegistro"));
        colAccion.setCellValueFactory(new PropertyValueFactory<>("tipoAccion"));
        colLlave.setCellValueFactory(new PropertyValueFactory<>("codigoLlave"));
        colDocente.setCellValueFactory(new PropertyValueFactory<>("nombreCompletoDocente"));
        colConserje.setCellValueFactory(new PropertyValueFactory<>("usuarioConserje"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Formateo dinámico localizado para la marca de tiempo de la auditoría
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        configurarColumnaFecha();

        // 5. ASIGNACIÓN DEL CONTENEDOR OBSERVABLE A LA TABLA
        tablaPreview.setItems(viewModel.getPreviewData());

        // 6. LANZAR UNA CARGA INICIAL POR DEFECTO (Carga el historial de la semana actual)
        viewModel.cargarVistaPrevia();
    }

    /**
     * Aplica un formateador amigable a la celda de la fecha de transacción.
     */
    private void configurarColumnaFecha() {
        colFecha.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(formatter.format(fecha));
                }
            }
        });
    }

    /**
     * Disparador vinculado al botón "Ver Datos".
     */
    @FXML
    private void handleVistaPrevia() {
        viewModel.cargarVistaPrevia();
    }

    /**
     * Disparador vinculado al botón "Exportar PDF".
     * Abre un explorador de archivos del sistema operativo para guardar el informe de JasperReports de forma segura.
     */
    @FXML
    private void handleExportarPdf() {
        // Instanciamos el FileChooser nativo de la API gráfica de JavaFX
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Informe de Auditoría");

        // Sugerimos un nombre de archivo dinámico basado en la fecha del sistema actual
        String nombrePorDefecto = "Informe_Actividad_" + java.time.LocalDate.now() + ".pdf";
        fileChooser.setInitialFileName(nombrePorDefecto);

        // Forzamos al sistema operativo a filtrar para evitar extensiones inválidas accidentales
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf")
        );

        // Obtenemos una referencia de la ventana actual para renderizar el diálogo encima de forma modal
        Stage stage = (Stage) btnExportarPdf.getScene().getWindow();
        File archivoSeleccionado = fileChooser.showSaveDialog(stage);

        // Si el usuario seleccionó una ruta válida y no canceló el diálogo, ordenamos la descarga de bytes
        if (archivoSeleccionado != null) {
            viewModel.descargarYGuardarPDF(archivoSeleccionado);
        }
    }
}