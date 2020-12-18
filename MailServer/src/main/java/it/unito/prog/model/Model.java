package it.unito.prog.model;

import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.model.modules.database.DatabaseManager;
import it.unito.prog.model.modules.observables.ObservablesManager;
import it.unito.prog.model.modules.synchronize.SynchronizeManager;

public interface Model {
    ConfigManager configManager();

    DatabaseManager databaseManager();

    ObservablesManager<String> observablesManager();

    SynchronizeManager<String> synchronizeManager();
}
