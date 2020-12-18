package it.unito.prog.model;

import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.model.modules.config.ConfigManagerFactory;
import it.unito.prog.model.modules.database.DatabaseManager;
import it.unito.prog.model.modules.database.DatabaseManagerFactory;
import it.unito.prog.model.modules.observables.ObservablesManager;
import it.unito.prog.model.modules.observables.ObservablesManagerFactory;
import it.unito.prog.model.modules.synchronize.SynchronizeManager;
import it.unito.prog.model.modules.synchronize.SynchronizeManagerFactory;

class ModelImpl implements Model {
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final ObservablesManager<String> observablesManager;
    private final SynchronizeManager<String> synchronizeManager;

    ModelImpl() {
        observablesManager = ObservablesManagerFactory.newInstance();
        synchronizeManager = SynchronizeManagerFactory.newInstance();
        configManager = ConfigManagerFactory.newInstance();
        databaseManager = DatabaseManagerFactory.newInstance(configManager.read("db.path"));
    }

    @Override
    public ConfigManager configManager() {
        return configManager;
    }

    @Override
    public DatabaseManager databaseManager() {
        return databaseManager;
    }

    @Override
    public ObservablesManager<String> observablesManager() {
        return observablesManager;
    }

    @Override
    public SynchronizeManager<String> synchronizeManager() {
        return synchronizeManager;
    }
}
