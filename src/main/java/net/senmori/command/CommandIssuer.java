package net.senmori.command;

import java.io.File;

public interface CommandIssuer {
    Process issueCommand(File workDir, String... command);

    void executeCommand(File workDir, String... command);
}
