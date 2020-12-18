package it.unito.prog.controllers;

import it.unito.prog.Main;
import it.unito.prog.lib.enums.MailFolder;
import it.unito.prog.model.modules.client.ClientMail;
import it.unito.prog.model.modules.observables.Observables;
import it.unito.prog.model.modules.observables.ObservablesManager;
import it.unito.prog.scene.SceneSwitcher;
import javafx.fxml.Initializable;

public abstract class BaseController implements Initializable {
    protected static String waitingForInput = "Waiting for input...";
    private static String currentUser = null;

    protected static String getCurrentUser() {
        if (currentUser == null)
            currentUser = Main.appModel().configManager().read("user.email");
        return currentUser;
    }

    protected static MailFolder getCurrentFolder() {
        return (MailFolder) observablesManager().getObject(Observables.CURRENT_FOLDER).getValue();
    }

    protected static SceneSwitcher sceneSwitcher() {
        return SceneSwitcher.getInstance();
    }

    protected static ObservablesManager<Observables> observablesManager() {
        return Main.appModel().observablesManager();
    }

    protected static ClientMail clientMail() {
        return Main.appModel().clientMail();
    }

    protected static void setStatusLabel(String string) {
        observablesManager().setObjectValue(Observables.STATUS_LABEL, string);
    }
}
