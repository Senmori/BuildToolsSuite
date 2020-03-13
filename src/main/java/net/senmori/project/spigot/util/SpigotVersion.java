package net.senmori.project.spigot.util;

import net.senmori.versioning.ComparableVersion;

public class SpigotVersion implements Comparable<SpigotVersion> {

    private final ComparableVersion version;
    private String displayString;


    public SpigotVersion(ComparableVersion version) {
        this.version = version;
        this.displayString = version.toString();
    }

    private SpigotVersion(ComparableVersion version, String displayString) {
        this( version );
        this.displayString = displayString;
    }

    public String getAlias() {
        return displayString;
    }

    public String getVersionString() {
        return version.toString();
    }

    @Override
    public int compareTo(SpigotVersion other) {
        return this.version.compareTo( other.version );
    }

    @Override
    public boolean equals(Object obj) {
        return ( obj instanceof SpigotVersion ) && ( this.compareTo( ( SpigotVersion ) obj ) == 0 );
    }

    public String toString() {
        return version.toString();
    }
}
