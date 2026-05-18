package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.InformeRequestDTO;
import com.iesjc.keymasterclient.models.InformeRowResponseDTO;
import com.iesjc.keymasterclient.services.InformeApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

/**
 * ViewModel encargado de la lógica de los informes de auditoría.
 * Gestiona los filtros, la vista previa y la descarga de documentos PDF.
 */
public class ReportesViewModel {

    // --- SERVICIOS ---
    private final InformeApiService informeService = new InformeApiService();

    // --- PROPIEDADES DE LOS FILTROS (UI) ---
    private final StringProperty tipoInforme = new SimpleStringProperty("Auditoría de Préstamos");
    private final StringProperty rangoRapido = new SimpleStringProperty("SEMANA"); // Por defecto

    // Usamos ObjectProperty para manejar las fechas complejas de JavaFX
    private final ObjectProperty<LocalDate> fechaInicio = new SimpleObjectProperty<>(LocalDate.now().minusDays(7));
    private final ObjectProperty<LocalDate> fechaFin = new SimpleObjectProperty<>(LocalDate.now());

    // Propiedad calculada: Será TRUE solo si el rangoRapido es "PERSONALIZADO"
    private final BooleanProperty isPersonalizado = new SimpleBooleanProperty(false);

    // --- LISTA PARA LA TABLA DE VISTA PREVIA ---
    private final ObservableList<InformeRowResponseDTO> previewData = FXCollections.observableArrayList();

    // --- ESTADOS Y MENSAJES ---
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty successMessage = new SimpleStringProperty("");

    public ReportesViewModel() {
        // Listener para activar/desactivar los selectores de fecha según el desplegable
        rangoRapido.addListener((obs, oldVal, newVal) -> {
            isPersonalizado.set("PERSONALIZADO".equalsIgnoreCase(newVal));
            // Borramos los mensajes al cambiar de filtro
            errorMessage.set("");
            successMessage.set("");
        });
    }

    /**
     * Construye el DTO con los filtros actuales seleccionados por el usuario.
     */
    private InformeRequestDTO construirRequest() {
        return InformeRequestDTO.builder()
                .tipoInforme(tipoInforme.get())
                .rangoRapido(rangoRapido.get())
                // Solo enviamos fechas si el modo es personalizado
                .fechaInicio(isPersonalizado.get() ? fechaInicio.get() : null)
                .fechaFin(isPersonalizado.get() ? fechaFin.get() : null)
                .build();
    }

    /**
     * Llama al servicio para obtener los datos crudos y llenar la tabla de la vista previa.
     */
    public void cargarVistaPrevia() {
        // Pequeña validación si es personalizado
        if (isPersonalizado.get() && (fechaInicio.get() == null || fechaFin.get() == null)) {
            errorMessage.set("Debes seleccionar una fecha de inicio y una de fin válidas.");
            return;
        }

        isLoading.set(true);
        errorMessage.set("");
        successMessage.set("");

        informeService.obtenerVistaPrevia(construirRequest())
                .thenAccept(filas -> Platform.runLater(() -> {
                    previewData.setAll(filas);
                    isLoading.set(false);
                    successMessage.set("Vista previa cargada: " + filas.size() + " registros encontrados.");
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Error al cargar la vista previa. Revisa la conexión.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    /**
     * Descarga el PDF del servidor, lo guarda en la ruta indicada y lo abre.
     * @param archivoDestino Objeto File elegido por el usuario mediante un FileChooser.
     */
    public void descargarYGuardarPDF(File archivoDestino) {
        if (archivoDestino == null) return; // El usuario canceló el diálogo de guardar

        isLoading.set(true);
        errorMessage.set("");
        successMessage.set("");

        informeService.descargarPdfInforme(construirRequest())
                .thenAccept(pdfBytes -> {
                    try {
                        // 1. Escribir los bytes recibidos en el disco duro físico
                        try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                            fos.write(pdfBytes);
                            fos.flush();
                        }

                        // 2. Abrir el archivo generado usando el lector de PDF predeterminado del S.O.
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(archivoDestino);
                        }

                        Platform.runLater(() -> {
                            isLoading.set(false);
                            successMessage.set("Informe PDF generado y guardado con éxito.");
                        });

                    } catch (Exception ioException) {
                        Platform.runLater(() -> {
                            isLoading.set(false);
                            errorMessage.set("Error al guardar o abrir el archivo en tu ordenador.");
                            ioException.printStackTrace();
                        });
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("El servidor no pudo generar el PDF. Verifica que existan datos.");
                        ex.printStackTrace();
                    });
                    return null;
                });
    }

    // --- GETTERS PARA BINDING CON JAVAFX ---

    public StringProperty tipoInformeProperty() { return tipoInforme; }
    public StringProperty rangoRapidoProperty() { return rangoRapido; }
    public ObjectProperty<LocalDate> fechaInicioProperty() { return fechaInicio; }
    public ObjectProperty<LocalDate> fechaFinProperty() { return fechaFin; }
    public BooleanProperty isPersonalizadoProperty() { return isPersonalizado; }

    public ObservableList<InformeRowResponseDTO> getPreviewData() { return previewData; }

    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public StringProperty successMessageProperty() { return successMessage; }
}