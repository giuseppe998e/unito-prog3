package it.unito.prog.model.modules.config;

public interface ConfigManager {
    void save();

    void reload();

    String read(String p);

    void set(String p, String v);
}
