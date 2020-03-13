package net.senmori.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import net.senmori.Main;

public class LogHandler {
    private static final Logger log = Logger.getLogger(LogHandler.class.getName());

    public static void log(Level level, String message) {
        Platform.runLater(() -> log.log(level, message));
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void debug(String message) {
        if ( false ) {
            log(Level.FINE, message);
        }
    }

    public static void warn(String message) {
        log(Level.WARNING, message);
    }

    public static void error(String message) {
        log(Level.SEVERE, message);
    }
}
