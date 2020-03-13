package net.senmori.project.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.text.WordUtils;

public enum ReleaseType {
    SNAPSHOT( "snapshot" ),
    RELEASE( "release" ),
    OLD_BETA( "old_beta" ),
    OLD_ALPHA( "old_alpha" );

    private static final Map<String, ReleaseType> formattedMap = Maps.newHashMap();
    private final String name;

    private ReleaseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return WordUtils.capitalize( getName().replaceAll( "_", " " ) );
    }


    public static ReleaseType getByName(String name) {
        for ( ReleaseType type : values() ) {
            if ( type.getName().equalsIgnoreCase( name ) ) {
                return type;
            }
        }
        return null;
    }

    public static ReleaseType getByFormattedName(String string) {
        return formattedMap.getOrDefault( string, ReleaseType.RELEASE );
    }

    static {
        for ( ReleaseType type : values() ) {
            formattedMap.put( type.getFormattedName(), type );
        }
    }
}
