package it.unito.prog.model.modules.config;

public final class ConfigManagerFactory {
    public static ConfigManager newInstance() {
        return new ConfigManagerImpl();
    }
}
