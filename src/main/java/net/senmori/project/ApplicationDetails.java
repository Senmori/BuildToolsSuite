package net.senmori.project;

import java.io.File;
import javafx.application.Application;
import net.senmori.Main;
import net.senmori.storage.Directory;
import net.senmori.util.SystemUtil;

public class ApplicationDetails {

    private final Application application;
    private final Directory workingDirectory;

    public ApplicationDetails(Application application) {
        this.application = application;
        this.workingDirectory = findWorkingDirectory();
        getWorkingDirectory().getFile().mkdirs();
    }

    private Directory findWorkingDirectory() {
        String userHomeDirectory = System.getProperty("user.home");
        if (SystemUtil.isWindows()) {
            return new Directory(userHomeDirectory, File.separator + "Documents" + File.separator + "BTSuite");
        }
        return new Directory(userHomeDirectory, "BTSuite");
    }

    public Application getApplication() {
        return application;
    }

    public Directory getWorkingDirectory() {
        return workingDirectory;
    }

    public ClassLoader getApplicationClassLoader() {
        return Main.class.getClassLoader();
    }
}
