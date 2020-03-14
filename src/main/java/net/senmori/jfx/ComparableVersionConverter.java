package net.senmori.jfx;

import com.electronwill.nightconfig.core.conversion.Converter;
import java.util.function.Predicate;
import net.senmori.versioning.ComparableVersion;

public class ComparableVersionConverter implements Converter<ComparableVersion, String>, Predicate<Object> {
    @Override
    public ComparableVersion convertToField(String str) {
        return new ComparableVersion(str);
    }

    @Override
    public String convertFromField(ComparableVersion comparableVersion) {
        return comparableVersion.toString();
    }

    @Override
    public boolean test(Object version) {
        if(!(version instanceof ComparableVersion)) {
            return false;
        }
        ComparableVersion ver = (ComparableVersion) version;
        return ver != null;
    }
}
