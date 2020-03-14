package net.senmori.jfx;

import com.electronwill.nightconfig.core.Config;
import javafx.beans.property.SimpleObjectProperty;

public class ConfigProperty<T> extends SimpleObjectProperty<T> {

    private final Config config;
    private final String key;
    private final T defaultValue;

    public ConfigProperty(Config config, String key, T defaultValue) {
        super(config, key, defaultValue);
        this.config = config;
        this.key = key;
        this.defaultValue = defaultValue;

        addListener((obs, old, newValue) -> {
            if (newValue == null) {
                newValue = this.defaultValue;
            }
            if (old != newValue) {
                config.set(key, newValue);
            }
        });
    }

    /**
     * Forcefully refresh the config value from file
     */
    public void refreshValue() {
        set(config.get(key));
    }
}
