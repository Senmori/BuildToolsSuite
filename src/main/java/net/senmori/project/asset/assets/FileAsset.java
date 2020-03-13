package net.senmori.project.asset.assets;

import java.io.File;
import java.net.URI;
import net.senmori.project.asset.Asset;

public abstract class FileAsset implements Asset {

    private final URI assetLocation;
    protected FileAsset(File file) {
        this.assetLocation = file.toURI();
    }

    @Override
    public URI getAssetLocation() {
        return assetLocation;
    }

    public File getFile() {
        return new File(getAssetLocation());
    }
}
