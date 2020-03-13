package net.senmori.util;

public class SystemUtil {
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static boolean isAutocrlf() {
        return !"\n".equalsIgnoreCase(System.getProperty("line.separator"));
    }
}
