package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.PrestamoResponseDTO;
import com.iesjc.keymasterclient.services.PrestamoApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

/**
 * ViewModel para la gestión de los préstamos activos (transacciones en curso).
 */
public class PrestamosViewModel {

    // --- SERVICIOS ---
    private final PrestamoApiService prestamoService;

    // --- DATOS (Listas Observables) ---
    private final ObservableList<PrestamoResponseDTO> masterData = FXCollections.observableArrayList();
    private final FilteredList<PrestamoResponseDTO> filteredData = new FilteredList<>(masterData);

    // --- PROPIEDADES PARA BINDING (UI) ---
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty statusCountLabel = new SimpleStringProperty("0 préstamos activos");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public PrestamosViewModel() {
        this.prestamoService = new PrestamoApiService();
        configurarFiltros();
    }

    /**
     * Configura el buscador universal para la tabla de préstamos.
     */
    private void configurarFiltros() {
        searchText.addListener((obs, old, newValue) -> aplicarFiltros());
    }

    /**
     * Filtra los préstamos activos buscando coincidencias en el nombre del docente o el código de la llave.
     */
    private void aplicarFiltros() {
        filteredData.setPredicate(prestamo -> {
            String busqueda = searchText.get().toLowerCase().trim();
            if (busqueda.isEmpty()) {
                return true;
            }

            // Busca por el código de la llave ("A3.2.2") o por el nombre del docente ("Mercedes")
            boolean coincideLlave = prestamo.getCodigoLlave() != null &&
                    prestamo.getCodigoLlave().toLowerCase().contains(busqueda);

            boolean coincideDocente = prestamo.getNombreCompletoProfesor() != null &&
                    prestamo.getNombreCompletoProfesor().toLowerCase().contains(busqueda);

            return coincideLlave || coincideDocente;
        });

        actualizarContador();
    }

    /**
     * Carga todos los préstamos que actualmente están en curso (sin devolver).
     */
    public void cargarPrestamosActivos() {
        isLoading.set(true);
        errorMessage.set(""); // Limpiamos errores previos

        prestamoService.obtenerPrestamosActivos()
                .thenAccept(prestamos -> {
                    Platform.runLater(() -> {
                        masterData.setAll(prestamos);
                        aplicarFiltros();
                        isLoading.set(false);
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Error al cargar los préstamos activos.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    /**
     * Llama al servicio para devolver una llave y actualiza la lista al instante.
     */
    public void devolverLlave(Integer idPrestamo) {
        isLoading.set(true);

        prestamoService.finalizarPrestamo(idPrestamo)
                .thenAccept(prestamoFinalizado -> {
                    Platform.runLater(() -> {
                        // Al devolver la llave, recargamos la lista desde el servidor
                        // para garantizar que tenemos la versión más actual.
                        cargarPrestamosActivos();
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Error al procesar la devolución de la llave.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    private void actualizarContador() {
        statusCountLabel.set(filteredData.size() + " préstamos activos visualizados");
    }

    // --- GETTERS PARA BINDING ---
    public FilteredList<PrestamoResponseDTO> getFilteredData() { return filteredData; }
    public StringProperty searchTextProperty() { return searchText; }
    public StringProperty statusCountLabelProperty() { return statusCountLabel; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
}