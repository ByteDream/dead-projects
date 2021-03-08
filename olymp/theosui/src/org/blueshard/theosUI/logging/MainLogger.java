package org.blueshard.theosUI.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.blueshard.theosUI.utils.ConsoleColors;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class MainLogger {

    private final Logger logger = Logger.getLogger(MainLogger.class);

    public MainLogger(Level logLevel) throws IOException {
        new MainLogger(logLevel, true, false, "");
    }

    public MainLogger(Level logLevel, String filename) throws IOException {
        new MainLogger(logLevel, true, true, filename);
    }

    public MainLogger(Level logLevel, boolean logConsole, boolean logFile, String filename) throws IOException {
        logger.setLevel(logLevel);

        if (logConsole) {
            ConsoleAppender consoleAppender = new ConsoleAppender(new Layout() {
                @Override
                public String format(LoggingEvent loggingEvent) {
                    DateFormat date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.GERMANY);
                    Level level = loggingEvent.getLevel();
                    StringBuilder extraData = new StringBuilder();
                    String color;
                    ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();

                    switch (loggingEvent.getLevel().toInt()) {
                        case Level.WARN_INT:
                            color = ConsoleColors.RED_BRIGHT;
                        case Level.ERROR_INT:
                            color = ConsoleColors.RED_BOLD + ConsoleColors.RED_UNDERLINED;
                        case Level.FATAL_INT:
                            color = ConsoleColors.BLACK;
                        default:
                            color = "";
                    }

                    if (throwableInformation != null) {
                        extraData.append(ConsoleColors.RED);
                        Arrays.asList(throwableInformation.getThrowable().getStackTrace()).forEach(stackTraceElement -> extraData.append(stackTraceElement).append("\n"));
                    }
                    return color + "[" + date.format(loggingEvent.getTimeStamp()) + "] " + level + ": " + loggingEvent.getMessage() + "\n" + extraData.toString() + ConsoleColors.RESET;
                }

                @Override
                public boolean ignoresThrowable() {
                    return false;
                }

                @Override
                public void activateOptions() {

                }
            });
            logger.addAppender(consoleAppender);
        }
        if (logFile) {
            RollingFileAppender rollingFileAppender = new RollingFileAppender(new Layout() {
                @Override
                public String format(LoggingEvent loggingEvent) {
                    DateFormat date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.GERMANY);
                    Level level = loggingEvent.getLevel();
                    StringBuilder extraData = new StringBuilder();
                    ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();

                    if (throwableInformation != null) {
                        Arrays.asList(throwableInformation.getThrowable().getStackTrace()).forEach(stackTraceElement -> extraData.append(stackTraceElement).append("\n"));
                    }
                    return "[" + date.format(loggingEvent.getTimeStamp()) + "] " + level + ": " + loggingEvent.getMessage() + "\n" + extraData.toString();
                }

                @Override
                public boolean ignoresThrowable() {
                    return false;
                }

                @Override
                public void activateOptions() {

                }
            }, filename, true);
            logger.addAppender(rollingFileAppender);
        }
    }

    public Logger getLogger() {
        return logger;
    }

}
