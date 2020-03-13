package net.senmori.task;

import java.io.File;
import javafx.concurrent.Task;
import net.senmori.download.FileDownloader;

public class FileDownloadTask extends Task<File> {
    private final String url;
    private final File target;
    private final String finalName;

    public FileDownloadTask(String url, File target) {
        this( url, target, null );
    }

    public FileDownloadTask(String url, File target, String fileName) {
        this.url = url;
        this.target = target;
        this.finalName = fileName;
    }

    @Override
    public File call() throws Exception {
        return new FileDownloader().download( url, target, finalName );
    }
}
