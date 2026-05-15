package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.services.LlaveApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

/**
 * ViewModel para la gestión del inventario de llaves.
 * Implementa lógica de filtrado acumulativo (Buscador + Estado).
 */
public class InventarioViewModel {

    // --- SERVICIOS ---
    private final LlaveApiService llaveService;

    // --- DATOS (Listas Observables) ---
    private final ObservableList<LlaveResponseDTO> masterData = FXCollections.observableArrayList();
    // filteredData es una "vista" de masterData que se actualiza sola al filtrar
    private final FilteredList<LlaveResponseDTO> filteredData = new FilteredList<>(masterData);

    // --- PROPIEDADES PARA BINDING (UI) ---
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty selectedStatus = new SimpleStringProperty("Todas");
    private final StringProperty statusCountLabel = new SimpleStringProperty("Mostrando 0 llaves");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public InventarioViewModel() {
        this.llaveService = new LlaveApiService();
        configurarFiltros();
    }

    /**
     * Configura el predicado de la FilteredList para que escuche cambios
     * en el buscador y en el desplegable de estados.
     */
    private void configurarFiltros() {
        // Creamos un listener que reacciona tanto al texto como al estado
        searchText.addListener((obs, old, newValue) -> aplicarFiltros());
        selectedStatus.addListener((obs, old, newValue) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        filteredData.setPredicate(llave -> {
            // 1. Lógica del buscador (Código o Espacio)
            String busqueda = searchText.get().toLowerCase();
            boolean coincideTexto = busqueda.isEmpty() ||
                    llave.getCodigoInterno().toLowerCase().contains(busqueda) ||
                    llave.getCodigoEspacio().toLowerCase().contains(busqueda);

            // 2. Lógica del filtro de estado (ENUM)
            String estadoFiltro = selectedStatus.get();
            boolean coincideEstado = estadoFiltro.equals("Todas") ||
                    llave.getEstado().equals(estadoFiltro);

            return coincideTexto && coincideEstado; // Filtro acumulativo
        });

        actualizarContador();
    }

    /**
     * Carga las llaves desde el backend de forma asíncrona.
     */
    public void cargarLlaves() {
        isLoading.set(true);
        llaveService.obtenerTodasLasLlaves()
                .thenAccept(llaves -> {
                    Platform.runLater(() -> {
                        masterData.setAll(llaves);
                        actualizarContador();
                        isLoading.set(false);
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> isLoading.set(false));
                    ex.printStackTrace();
                    return null;
                });
    }

    private void actualizarContador() {
        statusCountLabel.set("Mostrando " + filteredData.size() + " llaves");
    }

    // --- GETTERS PARA BINDING ---

    public ObservableList<LlaveResponseDTO> getFilteredData() {
        return filteredData;
    }

    public StringProperty searchTextProperty() { return searchText; }
    public StringProperty selectedStatusProperty() { return selectedStatus; }
    public StringProperty statusCountLabelProperty() { return statusCountLabel; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
}