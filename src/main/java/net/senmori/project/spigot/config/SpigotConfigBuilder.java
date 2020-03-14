package net.senmori.project.spigot.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import net.senmori.project.asset.assets.ConfigurationFileAsset;
import net.senmori.project.asset.assets.JarFileAsset;
import net.senmori.project.asset.assets.LocalFileAsset;
import net.senmori.project.config.ConfigurationOptions;

public final class SpigotConfigBuilder {

    private CommentedConfig config = TomlFormat.newConcurrentConfig();
    private ConfigWriter configWriter = new TomlWriter();
    private ConfigParser<CommentedConfig> configParser = new TomlParser();
    private FileNotFoundAction fileNotFoundAction = FileNotFoundAction.CREATE_EMPTY;
    private LocalFileAsset localFileAsset;
    private JarFileAsset jarFileAsset;

    /**
     * Get a new instance of a {@link SpigotConfigBuilder} to configure
     * a {@link SpigotConfig}
     *
     * @param existingConfigFile the existing config file on disk
     * @return a new {@link SpigotConfigBuilder}
     */
    public static SpigotConfigBuilder builder(LocalFileAsset existingConfigFile) {
        return new SpigotConfigBuilder(existingConfigFile);
    }

    private SpigotConfigBuilder(LocalFileAsset localFileAsset) {
        this.localFileAsset = localFileAsset;
    }

    /**
     * Set the source file where the file will be copied from if no
     * file was found on disk.
     *
     * @param asset the {@link JarFileAsset} of the file
     * @return this
     */
    public SpigotConfigBuilder sourceFile(JarFileAsset asset) {
        this.jarFileAsset = asset;
        return this;
    }

    /**
     * Set the type of {@link CommentedConfig} for the {@link SpigotConfig}
     *
     * @param config the type of config
     * @return this
     */
    public SpigotConfigBuilder config(CommentedConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Set the type {@link ConfigWriter} for the {@link SpigotConfig}
     *
     * @param writer the writer to use
     * @return this
     */
    public SpigotConfigBuilder writer(ConfigWriter writer) {
        this.configWriter = writer;
        return this;
    }

    /**
     * Set the {@link ConfigParser} for the {@link SpigotConfig}
     *
     * @param parser the parser to use
     * @return this
     */
    public SpigotConfigBuilder parser(ConfigParser<CommentedConfig> parser) {
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
    public SpigotConfigBuilder fileNotFoundAction(FileNotFoundAction action) {
        this.fileNotFoundAction = action;
        return this;
    }

    /**
     * Set that the configuration should load the source file from
     * {@link JarFileAsset}'s location.
     *
     * @return this
     */
    public SpigotConfigBuilder copySourceFileOnLoad() {
        Objects.requireNonNull(jarFileAsset, () -> "Cannot copy jar file asset without a jar file");
        try {
            URL assetURL = jarFileAsset.getAssetLocation().toURL();
            this.fileNotFoundAction = FileNotFoundAction.copyData(assetURL);
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
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
