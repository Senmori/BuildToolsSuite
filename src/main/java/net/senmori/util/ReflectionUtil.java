package net.senmori.util;

public final class ReflectionUtil {
    public static final int JAR_FILE_ASSET_DEPTH = 3;

    public static Class<?> getCallingClass(int depth) {
        return BTSuiteSecurityManager.INSTANCE.getClassContext()[ depth ];
    }
}
