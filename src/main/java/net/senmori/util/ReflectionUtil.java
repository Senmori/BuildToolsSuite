package net.senmori.util;

import net.senmori.Main;

public final class ReflectionUtil {

    /**
     * Get the calling class.
     *
     * @return the calling class, or {@link Main}'s class.
     */
    public static Class<?> getCallingClass() {
        return getCallingClass(Main.class);
    }

    /**
     * Get the calling class
     *
     * @param def the class to return if no calling class is found
     * @return the calling class, or {@link Main}'s class
     */
    public static Class<?> getCallingClass(Class<?> def) {
        String callerName = Thread.currentThread().getStackTrace()[2].getClassName();
        try {
            return Class.forName(callerName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return def;
    }
}
