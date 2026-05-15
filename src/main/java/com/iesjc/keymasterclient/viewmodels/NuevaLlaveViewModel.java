package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.models.LlaveRequestDTO;
import com.iesjc.keymasterclient.models.LlaveResponseDTO;
import com.iesjc.keymasterclient.services.LlaveApiService;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.Setter;

import java.util.function.Consumer;

public class NuevaLlaveViewModel {

    private final LlaveApiService llaveService = new LlaveApiService();

    // Propiedades de la vista
    private final StringProperty codigoInterno = new SimpleStringProperty("");
    private final StringProperty idEspacio = new SimpleStringProperty(""); // Lo tratamos como texto temporalmente
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    // Callback para avisar a la ventana principal de que recargue la tabla
    @Setter
    private Consumer<LlaveResponseDTO> onSuccessCallback;

    public void guardarLlave() {
        if (codigoInterno.get().isBlank() || idEspacio.get().isBlank()) {
            errorMessage.set("Todos los campos son obligatorios.");
            return;
        }

        int espacioId;
        try {
            espacioId = Integer.parseInt(idEspacio.get().trim());
        } catch (NumberFormatException e) {
            errorMessage.set("El ID del espacio debe ser un número válido.");
            return;
        }

        isLoading.set(true);
        errorMessage.set("");

        LlaveRequestDTO request = new LlaveRequestDTO(codigoInterno.get(), espacioId);

        llaveService.crearLlave(request)
                .thenAccept(nuevaLlave -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        if (onSuccessCallback != null) {
                            onSuccessCallback.accept(nuevaLlave); // Avisamos de que tod0 fue bien
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isLoading.set(false);
                        errorMessage.set("Error: El código ya existe o el espacio es inválido.");
                    });
                    return null;
                });
    }

    public StringProperty codigoInternoProperty() { return codigoInterno; }
    public StringProperty idEspacioProperty() { return idEspacio; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
}