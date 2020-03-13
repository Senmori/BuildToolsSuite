package net.senmori.task.git;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javafx.concurrent.Task;
import net.senmori.util.LogHandler;
import net.senmori.util.SystemUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.BatchingProgressMonitor;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitCloneTask extends Task<File> {

    private final String url;
    private final File target;

    public GitCloneTask(String url, File target) {
        this.url = url;
        this.target = target;
    }

    @Override
    public File call() throws Exception {
        LogHandler.info("Starting clone of " + url + " to " + target.getName());
        Git result = Git.cloneRepository()
                        .setURI( url )
                        .setDirectory( target )
                        .setProgressMonitor( getMonitor() )
                        .call();

        try ( result ) {
            StoredConfig config = result.getRepository().getConfig();
            config.setBoolean("core", null, "autocrlf", SystemUtil.isAutocrlf());
            config.save();

            LogHandler.info("Cloned git repository " + url + " to " + target.getName() + ". Current HEAD: " + commitHash(result));
        } catch ( IOException | GitAPIException e ) {
            e.printStackTrace();
        }
        return result.getRepository().getDirectory();
    }

    public String commitHash(Git repo) throws GitAPIException {
        return getOnlyElement( repo.log().setMaxCount( 1 ).call() );
    }

    private String getOnlyElement(Iterable<RevCommit> iter) {
        Iterator<RevCommit> iterator = iter.iterator();
        RevCommit first = iterator.next();
        if ( !iterator.hasNext() ) {
            return first.getName();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("expected one element but was: <").append(first);
        for ( int i = 0; i < 4 && iterator.hasNext(); i++ ) {
            sb.append(", ").append(iterator.next());
        }
        if ( iterator.hasNext() ) {
            sb.append( ", ..." );
        }
        sb.append( '>' );

        throw new IllegalArgumentException( sb.toString() );
    }

    private BatchingProgressMonitor getMonitor() {
        return new BatchingProgressMonitor() {
            @Override
            protected void onUpdate(String taskName, int workCurr) {
                StringBuilder s = new StringBuilder();
                format( s, taskName, workCurr );
                send( s );
            }

            @Override
            protected void onEndTask(String taskName, int workCurr) {
                StringBuilder s = new StringBuilder();
                format( s, taskName, workCurr );
                //s.append( "\n" ); //$NON-NLS-1$
                send( s );
            }

            private void format(StringBuilder s, String taskName, int workCurr) {
                //s.append( "\r" ); //$NON-NLS-1$
                s.append( taskName );
                s.append( ": " ); //$NON-NLS-1$
                while ( s.length() < 25 )
                    s.append( ' ' );
                s.append( workCurr );
            }

            @Override
            protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
                StringBuilder s = new StringBuilder();
                format( s, taskName, cmp, totalWork, pcnt );
                send( s );
            }

            @Override
            protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
                StringBuilder s = new StringBuilder();
                format( s, taskName, cmp, totalWork, pcnt );
                //s.append( "\n" ); //$NON-NLS-1$
                send( s );
            }

            private void format(StringBuilder s, String taskName, int cmp,
                                int totalWork, int pcnt) {
                //s.append( "\r" ); //$NON-NLS-1$
                s.append( taskName );
                s.append( ": " ); //$NON-NLS-1$
                while ( s.length() < 25 )
                    s.append( ' ' );

                String endStr = String.valueOf( totalWork );
                String curStr = String.valueOf( cmp );
                while ( curStr.length() < endStr.length() )
                    curStr = " " + curStr; //$NON-NLS-1$
                if ( pcnt < 100 )
                    s.append( ' ' );
                if ( pcnt < 10 )
                    s.append( ' ' );
                s.append( pcnt );
                s.append( "% (" ); //$NON-NLS-1$
                s.append( curStr );
                s.append( "/" ); //$NON-NLS-1$
                s.append( endStr );
                s.append( ")" ); //$NON-NLS-1$
            }

            private void send(StringBuilder s) {
                updateMessage( s.toString() );
            }
        };
    }
}
