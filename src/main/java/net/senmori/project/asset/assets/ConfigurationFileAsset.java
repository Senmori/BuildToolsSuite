package net.senmori.project.asset.assets;

import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import java.io.File;

/**
 * An asset that is used to support configuration files.
 * They are backed by two files:<br>
 * 1. A local file where the program is running (or wherever the working
 * directory is).<br>
 * 2. A source file; the file that is packaged with the executable that is used
 * only when the local file cannot be found.
 */
public class ConfigurationFileAsset extends FileAsset {

    private final JarFileAsset sourceFileAsset;
    public ConfigurationFileAsset(LocalFileAsset localFile, JarFileAsset sourceFile) {
        super(new File(localFile.getAssetLocation()));
        this.sourceFileAsset = sourceFile;
    }

    /**
     * Get the {@link JarFileAsset} that is backing this configuration file.
     *
     * @return the {@link JarFileAsset}
     */
    public JarFileAsset getSourceFileAsset() {
        return sourceFileAsset;
    }

    /**
     * Get the {@link File} that the {@link #getSourceFileAsset()} is
     * pointing to.
     *
     * @return the {@link File} that is actually attached to {@link JarFileAsset}
     */
    public File getSourceFile() {
        return getSourceFileAsset().getFile();
    }
}
