package it.unito.prog.model.modules.config;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

class ConfigManagerImpl implements ConfigManager {
    private final File configFile;
    private final Properties properties;
    private boolean configUpdated;

    ConfigManagerImpl() {
        configUpdated = false;
        properties = new Properties();
        configFile = new File(getJarDir(), "config.properties");

        if (!configFile.exists()) {
            set("user.email", "CHANGE_ME");
            set("user.password", "CHANGE_ME");
            set("server.host", "127.0.0.1");
            set("server.port", "26656");
            save();
        } else {
            reload();
        }
    }

    @Override
    public void save() {
        if (!configUpdated) return;

        try (OutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "Mail client configuration");
            configUpdated = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        save();
        try (InputStream input = new FileInputStream(configFile)) {
            properties.clear();
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String read(String p) {
        return properties.getProperty(p, "");
    }

    @Override
    public void set(String p, String v) {
        properties.setProperty(p, v);
        configUpdated = true;
    }

    private File getJarDir() {
        try {
            URI uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            return new File(uri).getParentFile();
        } catch (URISyntaxException ignored) {
            return new File(".");
        }
    }
}
