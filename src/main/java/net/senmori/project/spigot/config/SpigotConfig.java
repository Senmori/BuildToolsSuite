package net.senmori.project.spigot.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.io.ParsingMode;
import net.senmori.project.asset.assets.ConfigurationFileAsset;
import net.senmori.project.config.ConfigurationOptions;

public class SpigotConfig {

    private final ConfigurationOptions<CommentedConfig> configOptions;

    private final ConfigurationFileAsset configurationFileAsset;

    protected SpigotConfig(ConfigurationFileAsset configFileAsset, ConfigurationOptions<CommentedConfig> configuration, FileNotFoundAction action) {
        this.configurationFileAsset = configFileAsset;
        this.configOptions = configuration;
        configOptions.getParser().parse(configFileAsset.getFile(), configOptions.getConfig(), ParsingMode.REPLACE, action);
    }

    public CommentedConfig getConfig() {
        return configOptions.getConfig();
    }

    public ConfigWriter getWriter() {
        return configOptions.getWriter();
    }

    public ConfigParser<CommentedConfig> getParser() {
        return configOptions.getParser();
    }

    public ConfigurationFileAsset getConfigurationFileAsset() {
        return configurationFileAsset;
    }
}
