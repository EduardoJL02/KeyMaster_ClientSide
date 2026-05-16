package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.ProfesorRequestDTO;
import com.iesjc.keymasterclient.models.ProfesorResponseDTO;
import com.iesjc.keymasterclient.services.ProfesorApiService;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.function.Consumer;

public class NuevoDocenteViewModel {

    private final ProfesorApiService profesorService = new ProfesorApiService();

    // Propiedades enlazadas a los TextFields
    private final StringProperty dni = new SimpleStringProperty("");
    private final StringProperty nombre = new SimpleStringProperty("");
    private final StringProperty apellidos = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty idDepartamento = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    private Consumer<ProfesorResponseDTO> onSuccessCallback;

    public void setOnSuccessCallback(Consumer<ProfesorResponseDTO> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    public void guardarDocente() {
        // 1. Validación básica de campos vacíos
        if (dni.get().isBlank() || nombre.get().isBlank() || apellidos.get().isBlank() ||
                email.get().isBlank() || idDepartamento.get().isBlank()) {
            errorMessage.set("Todos los campos son obligatorios.");
            return;
        }

        // 2. Validación del ID de departamento
        int depId;
        try {
            depId = Integer.parseInt(idDepartamento.get().trim());
        } catch (NumberFormatException e) {
            errorMessage.set("El ID del departamento debe ser un número entero.");
            return;
        }

        isLoading.set(true);
        errorMessage.set("");

        // 3. Construcción del DTO de envío
        ProfesorRequestDTO request = new ProfesorRequestDTO(
                dni.get().trim(),
                nombre.get().trim(),
                apellidos.get().trim(),
                email.get().trim(),
                depId
        );

        // 4. Llamada al servicio HTTP
        profesorService.crearProfesor(request)
                .thenAccept(nuevoProfesor -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        if (onSuccessCallback != null) {
                            onSuccessCallback.accept(nuevoProfesor);
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        // TODO: especificar mas en el error dependiendo del tipo de excepción (ej. conflicto por DNI/Email, error de validación del departamento, etc.)
                        errorMessage.set("Error: El DNI o Email ya existen, o el departamento no es válido.");
                    });
                    return null;
                });
    }

    // Getters para el Binding
    public StringProperty dniProperty() { return dni; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty apellidosProperty() { return apellidos; }
    public StringProperty emailProperty() { return email; }
    public StringProperty idDepartamentoProperty() { return idDepartamento; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
}