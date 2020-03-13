package net.senmori.task;

import com.google.common.base.Predicate;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.concurrent.Task;
import net.senmori.Console;
import net.senmori.util.LogHandler;
import org.apache.commons.io.FilenameUtils;

public class ExtractFilesTask extends Task<Boolean> {
    private final File zipFile;
    private final File targetFolder;
    private final Predicate<String> filter;
    private final Console console;

    public ExtractFilesTask(File zipFile, File targetFolder, Console console, Predicate<String> filter) {
        this.zipFile = zipFile;
        this.targetFolder = targetFolder;
        this.filter = filter;
        this.console = console;
    }

    @Override
    protected Boolean call() throws Exception {
        targetFolder.mkdir();
        ZipFile zip = new ZipFile( zipFile );
        InputStream is = null;
        OutputStream out = null;
        LogHandler.info( "ZipFile: " + zip.getName() );
        try {
            for ( Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();

                if ( filter != null ) {
                    if ( !filter.apply( entry.getName() ) ) {
                        continue;
                    }
                }

                File outFile = new File( targetFolder, entry.getName() );

                if ( entry.isDirectory() ) {
                    outFile.mkdirs();
                    continue;
                }

                if ( outFile.getParentFile() != null ) {
                    outFile.getParentFile().mkdirs();
                }

                is = zip.getInputStream( entry );
                out = new FileOutputStream( outFile );
                try {
                    ByteStreams.copy( is, out );
                } finally {
                    is.close();
                    out.close();
                }
                //console.setOptionalText( FilenameUtils.getBaseName( outFile.getName() ) );
            }
        } finally {
            zip.close();
        }
        return true;
    }
}
