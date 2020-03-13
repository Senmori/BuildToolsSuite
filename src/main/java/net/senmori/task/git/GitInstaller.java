package net.senmori.task.git;

import java.io.File;
import javafx.concurrent.Task;
import net.senmori.command.CommandHandler;
import net.senmori.pool.TaskUtil;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.Directory;
import net.senmori.util.FileUtil;
import net.senmori.util.LogHandler;
import net.senmori.util.SystemUtil;

public class GitInstaller extends Task<Boolean> {
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;

    public GitInstaller(BuildToolsSettings settings) {
        this.settings = settings;
        this.dirs = settings.getDirectories();
    }

    @Override
    public Boolean call() {
        // check for normal git installation
        try {
            LogHandler.debug( "Checking for Git install location." );
            CommandHandler.getCommandIssuer().executeCommand( dirs.getWorkingDir().getFile(), "sh", "-c", "exit" );
            return true;
        } catch ( Exception e ) {
            LogHandler.info( "Git not found. Trying to install PortableGit" );
        }
        return doInstall();
    }

    private boolean doInstall() {
        try {
            if ( SystemUtil.isWindows() ) {
                File portableGitDir = dirs.getPortableGitDir().getFile();
                Directory portableGitExe = new Directory( dirs.getPortableGitDir().getFile().getAbsolutePath(), "PortableGit" );

                if ( portableGitExe.getFile().exists() && portableGitExe.getFile().isDirectory() ) {
                    dirs.setPortableGitDir( portableGitExe );
                    LogHandler.info( "Found PortableGit already installed at " + portableGitExe.getFile() );
                    return true;
                }

                if ( ! portableGitDir.exists() ) {
                    portableGitDir.mkdirs();
                    LogHandler.warn( "*** Could not find PortableGit executable, downloading. ***" );
                    portableGitDir = TaskUtil.asyncDownloadFile( settings.getGitInstallerLink(), portableGitDir );
                }
                if ( ! FileUtil.isDirectory( portableGitExe.getFile() ) ) {
                    portableGitExe.getFile().mkdirs();
                    // yes to all, silent, don't install.  Only -y seems to work
                    // ProcessRunner appends information we don't need
                    Runtime.getRuntime().exec( portableGitDir.getPath(), new String[] { "-y", "-gm2", "-nr" }, portableGitDir.getParentFile() );

                    LogHandler.warn( "*** Please note this is a beta feature, so if it does not work please also try a manual install valueOf git from https://git-for-windows.github.io/ ***" );
                    dirs.setPortableGitDir( portableGitExe );
                    LogHandler.info( "Successfully installed PortableGit to " + dirs.getPortableGitDir() );
                }
            } else { // end if windows check
                LogHandler.error( " Invalid Architecture! This program can only be run on Windows systems!" );
                return false; // Invalid Architecture
            }
            LogHandler.info( "Git installation success!" );
            return true;
        } catch ( Exception e ) {
            LogHandler.error("Failed to install git!");
            return false;
        }
    }
}
