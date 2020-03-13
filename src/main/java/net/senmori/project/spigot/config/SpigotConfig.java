package net.senmori.project.spigot.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import java.io.File;

public class SpigotConfig {

    private final CommentedConfig config = TomlFormat.newConcurrentConfig();
    private final TomlWriter writer = new TomlWriter();
    private final TomlParser parser = new TomlParser();

    private final File configFile;

    public SpigotConfig(File configFile) {
        this.configFile = configFile;
        parser.parse(configFile, this.config, ParsingMode.REPLACE, FileNotFoundAction.CREATE_EMPTY);
    }

    public CommentedConfig getConfig() {
        return config;
    }
}
