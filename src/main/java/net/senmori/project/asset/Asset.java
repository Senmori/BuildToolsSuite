package net.senmori.project.asset;

import java.net.URI;

/**
 * Any asset that a build system might need during runtime.
 */
public interface Asset {
    /**
     * @return the {@link URI} of the asset
     */
    URI getAssetLocation();
}
