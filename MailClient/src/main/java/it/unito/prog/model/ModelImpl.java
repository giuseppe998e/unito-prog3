package it.unito.prog.model;

import it.unito.prog.model.modules.client.ClientMail;
import it.unito.prog.model.modules.client.ClientMailFactory;
import it.unito.prog.model.modules.config.ConfigManager;
import it.unito.prog.model.modules.config.ConfigManagerFactory;
import it.unito.prog.model.modules.observables.Observables;
import it.unito.prog.model.modules.observables.ObservablesManager;
import it.unito.prog.model.modules.observables.ObservablesManagerFactory;

class ModelImpl implements Model {
    private final ObservablesManager<Observables> observablesManager;
    private final ConfigManager configManager;
    private final ClientMail clientMail;

    public ModelImpl() {
        observablesManager = ObservablesManagerFactory.newInstance();
        configManager = ConfigManagerFactory.newInstance();
        clientMail = ClientMailFactory.newInstance(configManager);
    }

    @Override
    public ConfigManager configManager() {
        return configManager;
    }

    @Override
    public ObservablesManager<Observables> observablesManager() {
        return observablesManager;
    }

    @Override
    public ClientMail clientMail() {
        return clientMail;
    }
}
