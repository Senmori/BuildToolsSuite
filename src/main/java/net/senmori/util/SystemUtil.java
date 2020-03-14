package net.senmori.util;

import java.io.File;
import net.senmori.storage.Directory;

public class SystemUtil {
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static boolean isAutocrlf() {
        return !"\n".equalsIgnoreCase(System.getProperty("line.separator"));
    }

    public static Directory getWorkingDirectory() {
        String workingDir = System.getProperty("user.home");
        if (isWindows())
            return new Directory(workingDir, "/Documents/BTSuite");
        return new Directory(workingDir, "/BTSuite");
    }
}
