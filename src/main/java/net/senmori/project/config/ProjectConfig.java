package net.senmori.project.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.utils.ConfigWrapper;
import java.io.File;
import lombok.NonNull;

public class ProjectConfig<K> extends ConfigWrapper<Config> {

    private final ConfigurationOptions options;

    protected ProjectConfig(Config config, @NonNull ConfigurationOptions options) {
        super(config);
        this.options = options;
        File configFile = options.getFileAsset().getFile();
        options.getParser().parse(configFile, config, ParsingMode.REPLACE, options.getFileNotFoundAction());
    }

}
