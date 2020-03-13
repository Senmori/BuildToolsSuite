package net.senmori.pool;

import java.io.File;
import java.util.concurrent.ExecutionException;
import net.senmori.task.FileDownloadTask;

public class TaskUtil {
    /**
     * Download a File from a given url and block while retreiving it.
     *
     * @param url the url to download from
     * @param target the target file
     * @return the target file
     */
    public static File asyncDownloadFile(String url, File target) {
        try {
            FileDownloadTask task = new FileDownloadTask( url, target );
            TaskPools.submit( task );
            return task.get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return null;
    }
}
