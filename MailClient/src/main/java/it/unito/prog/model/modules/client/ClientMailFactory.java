package it.unito.prog.model.modules.client;

import it.unito.prog.model.modules.config.ConfigManager;

public final class ClientMailFactory {
    public static ClientMail newInstance(ConfigManager configManager) {
        return new ClientMailImpl(configManager);
    }
}
