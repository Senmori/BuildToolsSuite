package net.senmori.util;

import net.senmori.project.asset.assets.JarFileAsset;

public final class ReflectionUtil {
    /**
     * '3' represents the classes that are called before the calling class of
     * a {@link JarFileAsset} method was called.<br>
     * i.e. Main [via JarFileAsset.of(...)] > JarFileAsset > ReflectionUtil ><br>
     * BTSuiteSecurityManager <br>
     * There are three(3) classes called before the calling class (Main) gets
     * the value.
     */
    public static final int JAR_FILE_ASSET_DEPTH = 3;

    /**
     * Get the calling class of this method. The value that a user needs will
     * vary on the call stack. (see {@link #JAR_FILE_ASSET_DEPTH})
     *
     * @param depth how many classes are between the desired class and
     *              {@link BTSuiteSecurityManager}
     * @return the class
     */
    public static Class<?> getCallingClass(int depth) {
        return BTSuiteSecurityManager.INSTANCE.getClassContext()[ depth ];
    }
}
