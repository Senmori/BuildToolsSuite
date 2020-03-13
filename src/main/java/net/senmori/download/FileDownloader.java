package net.senmori.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import lombok.Cleanup;

public class FileDownloader implements Downloader<File> {
    @Override
    public File download(String url, File target, String finalName) {
        try {
            URL con = new URL( url );
            @Cleanup InputStream stream = con.openStream();
            @Cleanup FileOutputStream fos = new FileOutputStream( target );
            @Cleanup ReadableByteChannel bis = Channels.newChannel( stream );
            fos.getChannel().transferFrom( bis, 0L, Long.MAX_VALUE );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        if ( finalName != null && ! finalName.trim().isEmpty() ) {
            target.renameTo( new File( target.getParent(), finalName ) );
        }
        return target;
    }
}
