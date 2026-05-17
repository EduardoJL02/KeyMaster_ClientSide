package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.PrestamoResponseDTO;
import com.iesjc.keymasterclient.viewmodels.PrestamosViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para la vista de Préstamos Activos.
 * Gestiona el enlace de datos, el formateo de marcas temporales y las acciones transaccionales.
 */
public class PrestamosViewController {

    // --- COMPONENTES ENLAZADOS AL FXML ---
    @FXML private TextField txtBuscador;
    @FXML private Button btnNuevoPrestamo;

    @FXML private TableView<PrestamoResponseDTO> tablaPrestamos;
    @FXML private TableColumn<PrestamoResponseDTO, Integer> colId;
    @FXML private TableColumn<PrestamoResponseDTO, LocalDateTime> colFecha;
    @FXML private TableColumn<PrestamoResponseDTO, String> colLlave;
    @FXML private TableColumn<PrestamoResponseDTO, String> colEspacio;
    @FXML private TableColumn<PrestamoResponseDTO, String> colDocente;
    @FXML private TableColumn<PrestamoResponseDTO, String> colObservaciones;
    @FXML private TableColumn<PrestamoResponseDTO, Void> colAcciones;

    @FXML private Label lblContador;
    @FXML private Label lblCargando;

    // Instanciamos el motor lógico de la pantalla
    private final PrestamosViewModel viewModel = new PrestamosViewModel();

    /**
     * Inicializador automático de JavaFX.
     */
    @FXML
    public void initialize() {
        // 1. ENLACES BIDIRECCIONALES Y MONODIRECCIONALES (Binding)
        txtBuscador.textProperty().bindBidirectional(viewModel.searchTextProperty());
        lblContador.textProperty().bind(viewModel.statusCountLabelProperty());
        lblCargando.visibleProperty().bind(viewModel.isLoadingProperty());

        // 2. CONFIGURACIÓN DE COLUMNAS ESTÁNDAR
        colId.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        colLlave.setCellValueFactory(new PropertyValueFactory<>("codigoLlave"));
        colEspacio.setCellValueFactory(new PropertyValueFactory<>("codigoEspacio"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Buscamos la propiedad virtual que calcula el nombre compuesto
        colDocente.setCellValueFactory(new PropertyValueFactory<>("nombreCompletoProfesor"));

        // 3. FORMATEO DINÁMICO DE LA COLUMNA DE FECHA/HORA
        colFecha.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        configurarColumnaFecha();

        // 4. INYECCIÓN DEL BOTÓN INTERACTIVO "DEVOLVER"
        configurarColumnaAcciones();

        // 5. ASIGNACIÓN DE LA LISTA REACTIVA A LA TABLA
        tablaPrestamos.setItems(viewModel.getFilteredData());

        // 6. CONFIGURACIÓN DEL BOTÓN SUPERIOR PARA ABRIR EL MODAL
        btnNuevoPrestamo.setOnAction(event -> abrirModalNuevoPrestamo());

        // 7. CARGA INICIAL ASÍNCRONA DESDE SPRING BOOT
        viewModel.cargarPrestamosActivos();
    }

    private void abrirModalNuevoPrestamo() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/NuevoPrestamoView.fxml"));
            javafx.scene.Parent root = loader.load();

            NuevoPrestamoViewController controlador = loader.getController();

            // Callback: Si la transacción se guarda con éxito, ordenamos al ViewModel principal recargar la lista
            controlador.setOnSuccessCallback(nuevoPrestamo -> {
                System.out.println("Transacción registrada ID: " + nuevoPrestamo.getIdPrestamo() + ". Refrescando activos...");
                viewModel.cargarPrestamosActivos();
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Registrar Entrega de Llave");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Bloquea la ventana padre de atrás
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Formatea la visualización de objetos LocalDateTime (dd/MM/yyyy HH:mm).
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
     * Renderiza un botón estilizado de "Devolver" que invoca el cierre de la transacción en el ViewModel.
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            // Diseñamos un botón corporativo llamativo y ergonómico para el conserje
            private final Button btnDevolver = new Button("Devolver");
            private final HBox container = new HBox(btnDevolver);

            {
                // Aplicamos estilos limpios con bordes suaves y cursor de mano
                btnDevolver.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4; -fx-font-weight: bold;");
                btnDevolver.setPadding(new Insets(5, 12, 5, 12));
                container.setAlignment(Pos.CENTER);

                // Evento al pulsar el botón de la celda
                btnDevolver.setOnAction(event -> {
                    // Recuperamos el DTO de la fila exacta donde se hizo clic
                    PrestamoResponseDTO prestamo = getTableView().getItems().get(getIndex());

                    // Deshabilitamos el botón un segundo para evitar doble pulsación accidental
                    btnDevolver.setDisable(true);

                    // Ordenamos al ViewModel tramitar la devolución de la llave
                    viewModel.devolverLlave(prestamo.getIdPrestamo());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Reseteamos el estado de deshabilitación por si la celda se reutiliza dinámicamente
                    btnDevolver.setDisable(false);
                    setGraphic(container);
                }
            }
        });
    }
}