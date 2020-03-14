package net.senmori.project.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;

public class ConfigurationOptions<T extends Config> {

    private final T config;
    private final ConfigWriter writer;
    private final ConfigParser<T> parser;
    public ConfigurationOptions(T config, ConfigWriter writer, ConfigParser<T> parser) {
        this.config = config;
        this.writer = writer;
        this.parser = parser;
    }

    public T getConfig() {
        return config;
    }

    public ConfigWriter getWriter() {
        return writer;
    }

    public ConfigParser<T> getParser() {
        return parser;
    }
}
