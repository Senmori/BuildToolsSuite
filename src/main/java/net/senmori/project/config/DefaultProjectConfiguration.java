package net.senmori.project.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.io.ParsingMode;
import net.senmori.project.asset.assets.ConfigurationFileAsset;

/**
 * A configuration object representing the Spigot project's configuration
 * settings.
 * <br>
 * This class should not be built directly; users should use
 * {@link ConfigBuilder} instead.
 */
public class DefaultProjectConfiguration {

    private final ConfigurationOptions<CommentedConfig> configOptions;
    private final ConfigurationFileAsset configurationFileAsset;

    /**
     * Use {@link ConfigBuilder} to create a {@link DefaultProjectConfiguration} instance.
     */
    public DefaultProjectConfiguration(ConfigurationFileAsset configFileAsset, ConfigurationOptions<CommentedConfig> configuration, FileNotFoundAction action) {
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
