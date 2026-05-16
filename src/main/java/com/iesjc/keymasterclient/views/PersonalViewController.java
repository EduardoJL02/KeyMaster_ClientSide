package com.iesjc.keymasterclient.views;

import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.viewmodels.PersonalViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controlador de la interfaz visual del Personal Docente.
 * Se encarga del Data Binding y del renderizado personalizado de las celdas de la tabla.
 */
public class PersonalViewController {

    // --- ELEMENTOS CONECTADOS AL FXML ---
    @FXML private TextField txtBuscador;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Button btnNuevoDocente;

    @FXML private TableView<ProfesorResponseDTO> tablaProfesores;
    @FXML private TableColumn<ProfesorResponseDTO, Integer> colId;
    @FXML private TableColumn<ProfesorResponseDTO, String> colDni;
    @FXML private TableColumn<ProfesorResponseDTO, String> colNombre;
    @FXML private TableColumn<ProfesorResponseDTO, String> colApellidos;
    @FXML private TableColumn<ProfesorResponseDTO, String> colEmail;
    @FXML private TableColumn<ProfesorResponseDTO, String> colDepartamento;
    @FXML private TableColumn<ProfesorResponseDTO, String> colEstado;
    @FXML private TableColumn<ProfesorResponseDTO, Void> colAcciones;

    @FXML private Label lblContador;
    @FXML private Label lblCargando;

    // Instanciamos el cerebro de la pantalla
    private final PersonalViewModel viewModel = new PersonalViewModel();

    /**
     * Métod0 nativo de JavaFX ejecutado automáticamente al cargar la vista.
     */
    @FXML
    public void initialize() {
        // 1. ENLACE DE DATOS (Data Binding) DE CONTROLES
        txtBuscador.textProperty().bindBidirectional(viewModel.searchTextProperty());
        cmbFiltroEstado.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        // 2. ENLACE DE ETIQUETAS INFORMATIVAS
        lblContador.textProperty().bind(viewModel.statusCountLabelProperty());
        lblCargando.visibleProperty().bind(viewModel.isLoadingProperty());

        // 3. ASIGNACIÓN DE PROPIEDADES A COLUMNAS ESTÁNDAR
        // Deben coincidir exactamente con los nombres de las variables de ProfesorResponseDTO
        colId.setCellValueFactory(new PropertyValueFactory<>("idProfesor"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<>("nombreDepartamento"));

        // 4. CONFIGURACIÓN DE COLUMNA DE ESTADO PERSONALIZADA (Colores e Iconos)
        configurarColumnaEstado();

        // 5. CONFIGURACIÓN DE COLUMNA DE ACCIONES (Botones de Editar/Eliminar en fila)
        configurarColumnaAcciones();

        // 6. VINCULACIÓN DE LA LISTA FILTRADA A LA TABLEVIEW
        tablaProfesores.setItems(viewModel.getFilteredData());

        // 7. CONFIGURACIÓN DE BOTÓN SUPERIOR
        btnNuevoDocente.setOnAction(event -> abrirModalNuevoDocente());

        // 8. DISPARO ASÍNCRONO DE CARGA DESDE EL BACKEND SPRING BOOT
        viewModel.cargarProfesores();
    }



    /**
     * Fabrica un CellFactory para pintar la celda de estado con círculos de colores.
     */
    private void configurarColumnaEstado() {
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Si la fila está vacía o el objeto es nulo, limpiamos la celda
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Recuperamos el objeto Profesor de la fila actual
                    ProfesorResponseDTO profesor = getTableView().getItems().get(getIndex());

                    Circle indicator = new Circle(6); // Círculo de 6px de radio (Icono vectorizado)
                    Label label = new Label();
                    label.setStyle("-fx-font-weight: bold;");

                    // Evaluamos el estado de alta (Soft Delete) del docente
                    if (!profesor.getActivo()) {
                        // Si activo == false, el docente está dado de baja del centro
                        indicator.setFill(Color.web("#dc3545")); // Rojo
                        label.setText("DADO DE BAJA");
                        label.setTextFill(Color.web("#dc3545"));
                    } else {
                        // Si está activo (alta), por defecto estará "SIN PRÉSTAMO"
                        // TODO: En el módulo de préstamos cruzaremos esto con la BD para pintar "CON PRÉSTAMO"
                        indicator.setFill(Color.web("#28a745")); // Verde
                        label.setText("SIN PRÉSTAMO");
                        label.setTextFill(Color.web("#28a745"));
                    }

                    // Contenedor horizontal para alinear el círculo y el texto limpiamente
                    HBox container = new HBox(8, indicator, label);
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    setGraphic(container);
                    setText(null); // Borramos el texto nativo para que use el contenedor gráfico
                }
            }
        });
    }

    /**
     * Inyecta los botones interactivos de Editar y Dar de Baja en cada fila.
     */
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox container = new HBox(10, btnEdit, btnDelete);

            {
                // Estilos CSS rápidos alineados con la estética general de la app
                btnEdit.setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #dc3545; -fx-cursor: hand;");
                container.setAlignment(javafx.geometry.Pos.CENTER);

                // Acción para el botón Editar (Lápiz)
                btnEdit.setOnAction(event -> {
                    ProfesorResponseDTO profesor = getTableView().getItems().get(getIndex());
                    System.out.println("Abriendo edición para el profesor: " + profesor.getNombre());
                });

                // Acción para el botón Dar de Baja (Papelera)
                btnDelete.setOnAction(event -> {
                    ProfesorResponseDTO profesor = getTableView().getItems().get(getIndex());
                    System.out.println("Solicitando baja lógica (Soft Delete) para el profesor: " + profesor.getNombre());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void abrirModalNuevoDocente() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/NuevoDocenteView.fxml"));
            javafx.scene.Parent root = loader.load();

            NuevoDocenteViewController controlador = loader.getController();

            // Recargamos la tabla al guardar con éxito
            controlador.setOnSuccessCallback(nuevoProfesor -> {
                System.out.println("Docente creado: " + nuevoProfesor.getNombre());
                viewModel.cargarProfesores();
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Añadir Nuevo Docente");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Evita clics fuera de la ventana
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}