package net.senmori.command;

public final class CommandHandler {
    private static final WindowsCommandIssuer WINDOWS = new WindowsCommandIssuer();

    public static CommandIssuer getCommandIssuer() {
        return WINDOWS;
    }
}
