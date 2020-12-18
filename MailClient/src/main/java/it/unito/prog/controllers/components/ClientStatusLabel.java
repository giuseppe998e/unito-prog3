package it.unito.prog.controllers.components;

import it.unito.prog.Main;
import it.unito.prog.model.modules.observables.Observables;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

public class ClientStatusLabel {
    public static void bind(Label clientStatusLabel) {
        SimpleObjectProperty<String> serverStatusString = Main.appModel().observablesManager().getObject(Observables.STATUS_LABEL);
        clientStatusLabel.textProperty().bind(serverStatusString);
    }
}
