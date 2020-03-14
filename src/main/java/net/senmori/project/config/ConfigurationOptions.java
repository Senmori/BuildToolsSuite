package net.senmori.project.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import net.senmori.project.asset.assets.ConfigurationFileAsset;

public class ConfigurationOptions<T extends ProjectConfig> {

    private final ConfigWriter writer;
    private final ConfigParser<? extends Config> parser;
    private final ConfigurationFileAsset file;
    private final FileNotFoundAction fileNotFoundAction;

    private ConfigurationOptions() {
        throw new UnsupportedOperationException("Cannot instantiate ConfigurationOptions with an empty constructor");
    }

    protected ConfigurationOptions(ConfigWriter writer, ConfigParser<? extends Config> parser, ConfigurationFileAsset file, FileNotFoundAction fileNotFoundAction) {
        this.writer = writer;
        this.parser = parser;
        this.file = file;
        this.fileNotFoundAction = fileNotFoundAction;
    }

    public ConfigWriter getWriter() {
        return writer;
    }

    public ConfigParser<? extends Config> getParser() {
        return parser;
    }

    public ConfigurationFileAsset getFileAsset() {
        return file;
    }

    public FileNotFoundAction getFileNotFoundAction() {
        return fileNotFoundAction;
    }
}
