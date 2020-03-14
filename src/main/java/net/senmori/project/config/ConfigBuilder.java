package net.senmori.project.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import java.io.File;
import java.util.Objects;
import net.senmori.project.asset.assets.ConfigurationFileAsset;
import net.senmori.project.asset.assets.JarFileAsset;
import net.senmori.project.asset.assets.LocalFileAsset;
import net.senmori.project.spigot.config.SpigotConfig;

/**
 * The default config builder. <br>
 * Default settings:<br>
 * Config: {@link CommentedConfig}<br>
 * Writer: {@link TomlWriter}<br>
 * Parser: {@link TomlParser}<br>
 * FileNotFoundAction: {@link FileNotFoundAction#CREATE_EMPTY}<br>
 * <br>
 * The source file ({@link #sourceFile(JarFileAsset)}) is null by default,
 * users must explicitly set it in order for {@link #copySourceFileOnLoad()}
 * to work, otherwise it will fail-fast.
 */
public final class ConfigBuilder {

    private CommentedConfig config = TomlFormat.newConcurrentConfig();
    private ConfigWriter configWriter = new TomlWriter();
    private ConfigParser<CommentedConfig> configParser = new TomlParser();
    private FileNotFoundAction fileNotFoundAction = FileNotFoundAction.CREATE_EMPTY;
    private LocalFileAsset localFileAsset = null;
    private JarFileAsset jarFileAsset = null;

    /**
     * Get a new instance of a {@link ConfigBuilder} to configure
     * a {@link SpigotConfig}
     *
     * @param existingConfigFile the existing config file on disk
     * @return a new {@link ConfigBuilder}
     */
    public static ConfigBuilder builder(LocalFileAsset existingConfigFile) {
        return new ConfigBuilder(existingConfigFile);
    }

    private ConfigBuilder(LocalFileAsset localFileAsset) {
        this.localFileAsset = localFileAsset;
    }

    /**
     * Set the source file where the file will be copied from if no
     * file was found on disk.
     *
     * @param asset the {@link JarFileAsset} of the file
     * @return this
     */
    public ConfigBuilder sourceFile(JarFileAsset asset) {
        this.jarFileAsset = asset;
        return this;
    }

    /**
     * Set the type of {@link CommentedConfig} for the {@link SpigotConfig}
     *
     * @param config the type of config
     * @return this
     */
    public ConfigBuilder config(CommentedConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Set the type {@link ConfigWriter} for the {@link SpigotConfig}
     *
     * @param writer the writer to use
     * @return this
     */
    public ConfigBuilder writer(ConfigWriter writer) {
        this.configWriter = writer;
        return this;
    }

    /**
     * Set the {@link ConfigParser} for the {@link SpigotConfig}
     *
     * @param parser the parser to use
     * @return this
     */
    public ConfigBuilder parser(ConfigParser<CommentedConfig> parser) {
        this.configParser = parser;
        return this;
    }

    /**
     * Set the {@link FileNotFoundAction} to use when the configuration file
     * was not found as per the {@link LocalFileAsset} URL
     *
     * @param action the action to use
     * @return this
     */
    public ConfigBuilder fileNotFoundAction(FileNotFoundAction action) {
        this.fileNotFoundAction = action;
        return this;
    }

    /**
     * Set that the configuration should load the source file from
     * {@link JarFileAsset}'s location.
     *
     * @return this
     */
    public ConfigBuilder copySourceFileOnLoad() {
        Objects.requireNonNull(jarFileAsset, () -> "Cannot copy jar file asset without a jar file");
        File sourceFile = jarFileAsset.getFile();
        this.fileNotFoundAction = FileNotFoundAction.copyData(sourceFile);
        return this;
    }

    /**
     * @return a new {@link SpigotConfig} that has not been populated with value
     */
    public SpigotConfig build() {
        ConfigurationOptions<CommentedConfig> options = new ConfigurationOptions<>(config, configWriter, configParser);
        ConfigurationFileAsset asset = new ConfigurationFileAsset(localFileAsset, jarFileAsset);
        return new SpigotConfig(asset, options, fileNotFoundAction);
    }
}
