package net.senmori.project.spigot.tasks;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import net.senmori.project.minecraft.VersionManifest;
import net.senmori.project.spigot.util.BuildTools;
import net.senmori.util.LogHandler;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;

public class InvalidateCacheTask extends Task<Boolean> {
    private final BuildTools buildTools;
    private final VersionManifest manifest;

    public InvalidateCacheTask(BuildTools buildTools, VersionManifest manifest) {
        this.buildTools = buildTools;
        this.manifest = manifest;
    }

    @Override
    public Boolean call() throws Exception {
        File workingDirectory = buildTools.getWorkingDirectory().getFile();
        LogHandler.info( "Deleting all files and directories in " + workingDirectory.getName() );
        // Use our own deletion method instead of apache so we can redirect output to where we want
        if ( !workingDirectory.exists() ) {
            throw new IllegalArgumentException( workingDirectory + " does not exist." );
        }
        if ( !workingDirectory.isDirectory() ) {
            throw new IllegalArgumentException( workingDirectory + " is not a directory" );
        }

        File[] files = workingDirectory.listFiles();
        if ( ( files == null ) || ( files.length < 1 ) ) {
            throw new IOException( "Failed to list contents of " + workingDirectory );
        }

        Stream.of( files ).forEach( this::delete );

        return true;
    }

    private void delete(File file) {
        if ( file.isDirectory() ) {
            File[] files = file.listFiles();
            if ( ( files == null ) || ( files.length < 1 ) ) {
                return;
            }
            for ( File in : files ) {
                if ( in.isDirectory() ) {
                    delete( in );
                } else {
                    updateMessage( FilenameUtils.getBaseName( in.getName() ) );
                    try {
                        FileDeleteStrategy.FORCE.delete( in );
                    } catch ( IOException e ) {

                    }
                }
            }
        }
        updateMessage( "Deleting " + FilenameUtils.getBaseName( file.getName() ) );
        try {
            FileDeleteStrategy.FORCE.delete( file );
        } catch ( IOException e ) {

        }
    }
}
