package net.senmori.task.git;

import net.senmori.command.CommandHandler;
import net.senmori.command.CommandIssuer;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.util.LogHandler;

public class GitConfigurationTask implements Runnable {
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;

    public GitConfigurationTask(BuildToolsSettings settings) {
        this.settings = settings;
        this.dirs = settings.getDirectories();
    }

    @Override
    public void run() {
        CommandIssuer commandHandler = CommandHandler.getCommandIssuer();
        try {
            commandHandler.executeCommand( dirs.getWorkingDir().getFile(), "git", "--version" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        try {
            commandHandler.executeCommand( dirs.getWorkingDir().getFile(), "git", "config", "--global", "--includes", "user.name" );
        } catch ( Exception ex ) {
            LogHandler.info( "Git name not set, setting it to default value." );
            try {
                commandHandler.executeCommand( dirs.getWorkingDir().getFile(), "git", "config", "--global", "user.name", "BuildToolsSuite" );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        try {
            commandHandler.executeCommand( dirs.getWorkingDir().getFile(), "git", "config", "--global", "--includes", "user.email" );
        } catch ( Exception ex ) {
            LogHandler.info( "Git email not set, setting it to default value." );
            try {
                commandHandler.executeCommand( dirs.getWorkingDir().getFile(), "git", "config", "--global", "user.email", "buildToolsSuite@null.spigotmc.org" );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
