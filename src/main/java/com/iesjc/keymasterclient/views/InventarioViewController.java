package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.viewmodels.InventarioViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class InventarioViewController {

    @FXML private TextField txtBuscador;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Button btnNuevaLlave;

    @FXML private TableView<LlaveResponseDTO> tablaLlaves;
    @FXML private TableColumn<LlaveResponseDTO, String> colCodigo;
    @FXML private TableColumn<LlaveResponseDTO, String> colEspacio;
    @FXML private TableColumn<LlaveResponseDTO, String> colEstado;
    @FXML private TableColumn<LlaveResponseDTO, Void> colAcciones;

    @FXML private Label lblContador;
    @FXML private Label lblCargando;

    private final InventarioViewModel viewModel = new InventarioViewModel();

    @FXML
    public void initialize() {
        // 1. Vincular controles de filtrado con el ViewModel
        txtBuscador.textProperty().bindBidirectional(viewModel.searchTextProperty());
        cmbFiltroEstado.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        // 2. Vincular etiquetas informativas
        lblContador.textProperty().bind(viewModel.statusCountLabelProperty());
        lblCargando.visibleProperty().bind(viewModel.isLoadingProperty());

        // 3. Configurar columnas básicas de la tabla
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoInterno"));
        colEspacio.setCellValueFactory(new PropertyValueFactory<>("codigoEspacio"));

        // 4. Configurar columna de ESTADO con diseño personalizado (Iconos y Colores)
        configurarColumnaEstado();

        // 5. Configurar columna de ACCIONES (Botones de Editar/Eliminar)
        configurarColumnaAcciones();

        // 6. Asignar los datos filtrados a la tabla
        tablaLlaves.setItems(viewModel.getFilteredData());

        // 7. Lanzar la carga inicial de datos desde el servidor
        viewModel.cargarLlaves();

        // Configurar el botón de añadir llave
        btnNuevaLlave.setOnAction(event -> abrirModalNuevaLlave());
    }

    private void abrirModalNuevaLlave() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/NuevaLlaveView.fxml"));
            javafx.scene.Parent root = loader.load();

            NuevaLlaveViewController controlador = loader.getController();

            // Cuando se guarde con éxito, recargamos la tabla
            controlador.setOnSuccessCallback(nuevaLlave -> {
                System.out.println("Llave creada con éxito. Recargando...");
                viewModel.cargarLlaves(); // Refresca los datos llamando al backend
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Añadir Nueva Llave");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un CellFactory para que la columna estado muestre un círculo de color y el texto.
     */
    private void configurarColumnaEstado() {
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Círculo indicador de estado
                    Circle indicator = new Circle(6);
                    Label label = new Label(estado);
                    label.setStyle("-fx-font-weight: bold;");

                    switch (estado) {
                        case "DISPONIBLE" -> { indicator.setFill(Color.web("#28a745")); label.setTextFill(Color.web("#28a745")); }
                        case "EN_USO" -> { indicator.setFill(Color.web("#007bff")); label.setTextFill(Color.web("#007bff")); }
                        case "PERDIDA" -> { indicator.setFill(Color.web("#dc3545")); label.setTextFill(Color.web("#dc3545")); }
                        case "MANTENIMIENTO" -> { indicator.setFill(Color.web("#fd7e14")); label.setTextFill(Color.web("#fd7e14")); }
                        default -> indicator.setFill(Color.GRAY);
                    }

                    HBox container = new HBox(8, indicator, label);
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Inyecta botones de acción en cada fila de la tabla.
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #dc3545; -fx-cursor: hand;");
                pane.setAlignment(javafx.geometry.Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    LlaveResponseDTO llave = getTableView().getItems().get(getIndex());
                    System.out.println("Editando llave: " + llave.getCodigoInterno());
                });

                btnDelete.setOnAction(event -> {
                    LlaveResponseDTO llave = getTableView().getItems().get(getIndex());
                    System.out.println("Eliminando llave: " + llave.getCodigoInterno());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
}