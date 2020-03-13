package net.senmori.project.asset.assets;

import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import java.io.File;

public class ConfigurationFileAsset extends FileAsset {

    private final JarFileAsset sourceFileAsset;
    public ConfigurationFileAsset(LocalFileAsset localFile, JarFileAsset sourceFile, FileNotFoundAction action) {
        super(new File(localFile.getAssetLocation()));
        this.sourceFileAsset = sourceFile;
    }

    public JarFileAsset getSourceFileAsset() {
        return sourceFileAsset;
    }

    public File getConfigurationFile() {

    }
}
