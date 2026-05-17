package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.models.PrestamoRequestDTO;
import com.iesjc.keymasterclient.models.PrestamoResponseDTO;
import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.services.LlaveApiService;
import com.iesjc.keymasterclient.services.PrestamoApiService;
import com.iesjc.keymasterclient.services.ProfesorApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ViewModel que orquesta la carga de llaves y profesores para registrar un nuevo préstamo.
 */
public class NuevoPrestamoViewModel {

    // --- SERVICIOS ---
    private final PrestamoApiService prestamoService = new PrestamoApiService();
    private final LlaveApiService llaveService = new LlaveApiService();
    private final ProfesorApiService profesorService = new ProfesorApiService();

    // --- LISTAS OBSERVABLES (Para los ComboBox) ---
    private final ObservableList<LlaveResponseDTO> llavesDisponibles = FXCollections.observableArrayList();
    private final ObservableList<ProfesorResponseDTO> profesoresActivos = FXCollections.observableArrayList();

    // --- PROPIEDADES DE LA VISTA (Selección del usuario) ---
    // Usamos ObjectProperty porque guardaremos el objeto completo seleccionado, no solo un texto
    private final ObjectProperty<LlaveResponseDTO> llaveSeleccionada = new SimpleObjectProperty<>();
    private final ObjectProperty<ProfesorResponseDTO> profesorSeleccionado = new SimpleObjectProperty<>();
    private final StringProperty observaciones = new SimpleStringProperty("");

    // --- ESTADOS DE LA UI ---
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    // --- CALLBACK ---
    private Consumer<PrestamoResponseDTO> onSuccessCallback;

    public void setOnSuccessCallback(Consumer<PrestamoResponseDTO> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    /**
     * Descarga simultáneamente el catálogo de llaves y de profesores.
     * Filtra los datos para mostrar solo lo elegible (Llaves Disponibles y Profesores de Alta).
     */
    public void cargarDatosIniciales() {
        isLoading.set(true);
        errorMessage.set("");

        // Lanzamos ambas peticiones a la vez (Concurrencia)
        CompletableFuture<List<LlaveResponseDTO>> futureLlaves = llaveService.obtenerTodasLasLlaves();
        CompletableFuture<List<ProfesorResponseDTO>> futureProfesores = profesorService.obtenerTodosLosProfesores();

        // Esperamos a que AMBAS terminen con allOf()
        CompletableFuture.allOf(futureLlaves, futureProfesores)
                .thenAccept(v -> {
                    // Extraemos los resultados
                    List<LlaveResponseDTO> todasLlaves = futureLlaves.join();
                    List<ProfesorResponseDTO> todosProfesores = futureProfesores.join();

                    // Filtramos usando Java Streams
                    List<LlaveResponseDTO> filtradas = todasLlaves.stream()
                            .filter(llave -> "DISPONIBLE".equals(llave.getEstado()))
                            .collect(Collectors.toList());

                    List<ProfesorResponseDTO> filtrados = todosProfesores.stream()
                            .filter(ProfesorResponseDTO::getActivo) // Solo profesores que no estén dados de baja
                            .collect(Collectors.toList());

                    // Actualizamos la UI en el hilo principal
                    Platform.runLater(() -> {
                        llavesDisponibles.setAll(filtradas);
                        profesoresActivos.setAll(filtrados);
                        isLoading.set(false);
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Error al descargar los catálogos. Revisa la conexión.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    /**
     * Construye el DTO y envía la petición de registro al servidor.
     */
    public void guardarPrestamo() {
        // 1. Validación de campos obligatorios
        if (llaveSeleccionada.get() == null || profesorSeleccionado.get() == null) {
            errorMessage.set("Debes seleccionar obligatoriamente una llave y un docente.");
            return;
        }

        isLoading.set(true);
        errorMessage.set("");

        // 2. Construcción del Request DTO
        PrestamoRequestDTO request = new PrestamoRequestDTO(
                llaveSeleccionada.get().getIdLlave(),
                profesorSeleccionado.get().getIdProfesor(),
                observaciones.get().trim()
        );

        // 3. Envío al servidor
        prestamoService.crearPrestamo(request)
                .thenAccept(nuevoPrestamo -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        if (onSuccessCallback != null) {
                            onSuccessCallback.accept(nuevoPrestamo); // Notificamos a la vista principal
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("No se pudo registrar el préstamo. Quizás la llave ya fue tomada.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    // --- GETTERS PARA BINDING ---
    public ObservableList<LlaveResponseDTO> getLlavesDisponibles() { return llavesDisponibles; }
    public ObservableList<ProfesorResponseDTO> getProfesoresActivos() { return profesoresActivos; }

    public ObjectProperty<LlaveResponseDTO> llaveSeleccionadaProperty() { return llaveSeleccionada; }
    public ObjectProperty<ProfesorResponseDTO> profesorSeleccionadoProperty() { return profesorSeleccionado; }
    public StringProperty observacionesProperty() { return observaciones; }

    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
}