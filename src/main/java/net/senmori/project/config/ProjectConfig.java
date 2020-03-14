package net.senmori.project.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.utils.CommentedConfigWrapper;
import java.io.File;
import lombok.NonNull;

public class ProjectConfig extends CommentedConfigWrapper<CommentedConfig> {

    private final ConfigurationOptions<ProjectConfig> options;
    protected ProjectConfig(@NonNull CommentedConfig config, ConfigurationOptions<ProjectConfig> options) {
        super(config);
        this.options = options;
        File configFile = options.getFileAsset().getFile();
        options.getParser().parse(configFile, config, ParsingMode.REPLACE, options.getFileNotFoundAction());
    }

}
