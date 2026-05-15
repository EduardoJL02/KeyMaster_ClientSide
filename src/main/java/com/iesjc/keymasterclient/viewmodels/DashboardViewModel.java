package com.iesjc.keymasterclient.viewmodels;

import com.iesjc.keymasterclient.core.SessionContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DashboardViewModel {

    private final StringProperty welcomeMessage = new SimpleStringProperty();

    public DashboardViewModel() {
        // Obtenemos el nombre del usuario que inició sesión
        String user = SessionContext.getInstance().getUsername();
        welcomeMessage.set("Bienvenido de nuevo, " + user);
    }

    public StringProperty welcomeMessageProperty() {
        return welcomeMessage;
    }
}
