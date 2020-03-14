package net.senmori.jfx;

import com.electronwill.nightconfig.core.Config;
import net.senmori.versioning.ComparableVersion;

public class VersionProperty extends ConfigProperty<ComparableVersion> {

    private final ComparableVersionConverter converter = new ComparableVersionConverter();
    public VersionProperty(Config config, String key, ComparableVersion defaultValue) {
        super(config, key, defaultValue);

        config.get(key);
    }
}
