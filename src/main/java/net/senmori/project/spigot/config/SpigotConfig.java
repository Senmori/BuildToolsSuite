package net.senmori.project.spigot.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import java.io.File;

public class SpigotConfig {

    private final CommentedConfig config = TomlFormat.newConcurrentConfig();
    private final ConfigWriter writer;
    private final ConfigParser<? super CommentedConfig> parser;

    private final File configFile;

    public SpigotConfig(File configFile, ConfigParser<? super CommentedConfig> parser, ConfigWriter configWriter) {
        this.configFile = configFile;
        parser.parse(configFile, this.config, ParsingMode.REPLACE, FileNotFoundAction.CREATE_EMPTY);
        this.writer = configWriter;
        this.parser = parser;
    }

    public SpigotConfig(File configFile) {
        this(configFile, new TomlParser(), new TomlWriter());
    }

    public CommentedConfig getConfig() {
        return config;
    }
}
