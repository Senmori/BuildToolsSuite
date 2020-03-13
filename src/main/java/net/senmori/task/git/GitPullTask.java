package net.senmori.task.git;

import java.io.File;
import javafx.concurrent.Task;
import net.senmori.Console;
import net.senmori.util.LogHandler;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.BatchingProgressMonitor;

public class GitPullTask extends Task<File> {
    private final Git repo;
    private final String ref;
    private final Console console;

    public GitPullTask(Git repo, String ref, Console console) {
        this.repo = repo;
        this.ref = ref;
        this.console = console;
    }

    @Override
    public File call() throws Exception {
        LogHandler.info("Pulling updates for " + repo.getRepository().getDirectory());

        repo.reset()
            .setRef( "origin/master" )
            .setMode( ResetCommand.ResetType.HARD )
            .call();

        repo.fetch()
            .setProgressMonitor( getMonitor() )
            .call();

        LogHandler.info("Successfully fetched updates!");

        repo.reset()
            .setRef( ref )
            .setMode( ResetCommand.ResetType.HARD )
            .call();

        if ( ref.equals("master") ) {
            repo.reset()
                .setRef( "origin/master" )
                .setMode( ResetCommand.ResetType.HARD )
                .call();
        }
        LogHandler.info("Checked out: " + ref);
        return repo.getRepository().getDirectory();
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
                s.append( "\n" ); //$NON-NLS-1$
                send( s );
            }

            private void format(StringBuilder s, String taskName, int workCurr) {
                s.append( "\r" ); //$NON-NLS-1$
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
                s.append( "\n" ); //$NON-NLS-1$
                send( s );
            }

            private void format(StringBuilder s, String taskName, int cmp, int totalWork, int pcnt) {
                s.append( "\r" ); //$NON-NLS-1$
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
                //console.setOptionalText( s.toString() );
            }
        };
    }
}
