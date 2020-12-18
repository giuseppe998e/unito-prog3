package it.unito.prog.model;

import it.unito.prog.model.modules.client.ClientMail;
import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.model.modules.observables.Observables;
import it.unito.prog.model.modules.observables.ObservablesManager;

public interface Model {
    ConfigManager configManager();

    ObservablesManager<Observables> observablesManager();

    ClientMail clientMail();
}
