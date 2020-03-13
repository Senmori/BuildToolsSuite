package net.senmori.task;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import net.senmori.pool.TaskPools;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.Directory;
import net.senmori.util.LogHandler;
import net.senmori.util.ZipUtil;
import org.apache.commons.io.FileUtils;

public class MavenInstaller extends Task<File> {
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;

    public MavenInstaller(BuildToolsSettings settings) {
        this.settings = settings;
        this.dirs = settings.getDirectories();
    }


    @Override
    public File call() {
        LogHandler.debug( "Checking for Maven install location." );
        if ( isInstalled() ) {
            File mvn = new File( System.getenv( "M2_HOME" ) );
            dirs.setMvnDir( new Directory( mvn.getParent(), mvn.getName() ) );
            LogHandler.info( "Maven is installed at " + dirs.getMvnDir().getFile().getAbsolutePath() );
            return dirs.getMvnDir().getFile();
        }

        File maven = dirs.getMvnDir().getFile();
        String mvnVersion = settings.getMavenVersion();
        if ( !maven.exists() ) {
            maven.mkdirs();
            LogHandler.info( "Maven does not exist, downloading. Please wait." );

            File mvnTemp = new File( dirs.getWorkingDir().getFile(), "mvn.zip" );

            try {
                String url = settings.getMvnInstallerLink();
                FileDownloadTask task = new FileDownloadTask( url, mvnTemp );
                task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                    updateMessage( newValue );
                } );
                task.setOnSucceeded( (worker) -> {
                    updateMessage( "" );
                } );
                TaskPools.submit( task );
                mvnTemp = task.get();

                ZipUtil.unzip( mvnTemp, dirs.getMvnDir().getFile() );
                dirs.setMvnDir( new Directory( dirs.getMvnDir(), "apache-maven-" + mvnVersion ) );
                mvnTemp.delete();
            } catch ( IOException | InterruptedException | ExecutionException e ) {
                e.printStackTrace();
                return null;
            }
        } else {
            // get inner folder
            maven = new File( dirs.getMvnDir().getFile(), "apache-maven-" + mvnVersion );
            if ( !maven.exists() ) {
                LogHandler.info( "Maven directory was found, but no maven installation!" );
                FileUtils.deleteQuietly( dirs.getMvnDir().getFile() ); // delete and ignore errors
                TaskPools.execute( new MavenInstaller( settings ) );
                this.cancel( true );
                return null;
            } else { // apache-maven-<version> exists
                LogHandler.info( "Local install of maven found!" );
                dirs.setMvnDir( new Directory( dirs.getMvnDir(), "apache-maven-" + mvnVersion ) );
            }
        }
        LogHandler.info( "Maven is installed at " + dirs.getMvnDir().getFile() );
        return dirs.getMvnDir().getFile();
    }

    private boolean isInstalled() {
        String m2Home = System.getenv("M2_HOME");
        return ( m2Home != null ) && new File( m2Home ).exists();
    }
}
