package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.services.ProfesorApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

/**
 * ViewModel para la gestión del personal docente.
 * Controla la carga asíncrona de profesores y el filtrado en tiempo real en la UI.
 */
public class PersonalViewModel {

    // --- SERVICIOS ---
    private final ProfesorApiService profesorService;

    // --- DATOS (Listas Observables) ---
    // masterData almacena la lista íntegra que viene del Backend
    private final ObservableList<ProfesorResponseDTO> masterData = FXCollections.observableArrayList();
    // filteredData es la "sublista" que la tabla mostrará y se filtra automáticamente en memoria
    private final FilteredList<ProfesorResponseDTO> filteredData = new FilteredList<>(masterData);

    // --- PROPIEDADES PARA BINDING (Vínculo directo con la Vista) ---
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty selectedStatus = new SimpleStringProperty("Todas");
    private final StringProperty statusCountLabel = new SimpleStringProperty("Mostrando 0 docentes");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public PersonalViewModel() {
        this.profesorService = new ProfesorApiService();
        configurarFiltros();
    }

    /**
     * Configura los listeners encargados de escuchar los cambios en el buscador
     * y en el ComboBox para disparar el filtrado de forma inmediata.
     */
    private void configurarFiltros() {
        searchText.addListener((obs, old, newValue) -> aplicarFiltros());
        selectedStatus.addListener((obs, old, newValue) -> aplicarFiltros());
    }

    /**
     * Evalúa cada fila de profesores según los criterios del buscador y del filtro de estado.
     */
    private void aplicarFiltros() {
        filteredData.setPredicate(profesor -> {
            // 1. FILTRO DE BUSCADOR UNIVERSAL (Case-Insensitive)
            // Busca coincidencias parciales en DNI, Nombre, Apellidos o Departamento
            String busqueda = searchText.get().toLowerCase().trim();
            boolean coincideTexto = busqueda.isEmpty() ||
                    (profesor.getDni() != null && profesor.getDni().toLowerCase().contains(busqueda)) ||
                    (profesor.getNombre() != null && profesor.getNombre().toLowerCase().contains(busqueda)) ||
                    (profesor.getApellidos() != null && profesor.getApellidos().toLowerCase().contains(busqueda)) ||
                    (profesor.getNombreDepartamento() != null && profesor.getNombreDepartamento().toLowerCase().contains(busqueda));

            // 2. FILTRO POR ESTADO (Reglas basadas en los requisitos del PDF)
            String estadoFiltro = selectedStatus.get();
            boolean coincideEstado = false;

            if (estadoFiltro.equals("Todas")) {
                coincideEstado = true; // No filtra por estado
            } else if (estadoFiltro.equals("Dados de Baja")) {
                coincideEstado = !profesor.getActivo(); // Soft delete activo (activo = false)
            } else {
                // Para "Con Préstamos" o "Sin Préstamos", los profesores deben estar de alta (activos)
                if (profesor.getActivo()) {
                    // Nota: En futuras iteraciones vincularemos esto a la lógica real de préstamos del DTO.
                    // Por ahora, si está de alta, asumimos por defecto que entran en la lista base.
                    if (estadoFiltro.equals("Sin Préstamos")) {
                        coincideEstado = true; // Provisional
                    } else if (estadoFiltro.equals("Con Préstamos")) {
                        coincideEstado = false; // Provisional
                    }
                }
            }

            // Ambos filtros deben cumplirse a la vez (Filtro Cruzado Acumulativo)
            return coincideTexto && coincideEstado;
        });

        actualizarContador();
    }

    /**
     * Solicita la lista de profesores al servidor en un hilo secundario.
     */
    public void cargarProfesores() {
        isLoading.set(true);
        profesorService.obtenerTodosLosProfesores()
                .thenAccept(profesores -> {
                    // Forzamos la actualización en el hilo de la interfaz gráfica
                    Platform.runLater(() -> {
                        masterData.setAll(profesores);
                        aplicarFiltros(); // Forzamos el procesado del filtro inicial
                        isLoading.set(false);
                    });
                })
                .exceptionally(ex -> {
                    // Controlamos la excepción para evitar bloqueos
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        System.err.println("Error al cargar profesores en el ViewModel:");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    /**
     * Actualiza el contador dinámico inferior de la vista.
     */
    private void actualizarContador() {
        statusCountLabel.set("Mostrando " + filteredData.size() + " docentes");
    }

    // --- GETTERS DE PROPIEDADES (Esenciales para el Data Binding del Controlador) ---

    public FilteredList<ProfesorResponseDTO> getFilteredData() {
        return filteredData;
    }

    public StringProperty searchTextProperty() { return searchText; }
    public StringProperty selectedStatusProperty() { return selectedStatus; }
    public StringProperty statusCountLabelProperty() { return statusCountLabel; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
}