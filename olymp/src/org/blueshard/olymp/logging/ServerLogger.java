package org.blueshard.olymp.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.blueshard.olymp.files.ServerFiles;
import org.blueshard.olymp.utils.ConsoleColors;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class ServerLogger {

    private final Logger logger = LogManager.getLogger(ServerLogger.class);

    public Logger main() throws IOException {
        logger.setLevel(Level.ALL);

        RollingFileAppender rollingFileAppender = new RollingFileAppender(new Layout() {
            @Override
            public String format(LoggingEvent loggingEvent) {
                SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
                StringBuilder extraData = new StringBuilder();
                ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();

                if (throwableInformation != null) {
                    Arrays.asList(throwableInformation.getThrowable().getStackTrace()).forEach(stackTraceElement -> extraData.append(stackTraceElement).append("\n"));
                }
                return "[" + date.format(loggingEvent.getTimeStamp()) + " - Server] " + loggingEvent.getLevel() + ": " + loggingEvent.getMessage() + "\n" + extraData.toString();
            }

            @Override
            public boolean ignoresThrowable() {
                return false;
            }

            @Override
            public void activateOptions() {

            }
        }, ServerFiles.logs.main_log, true);
        rollingFileAppender.setMaxBackupIndex(10);
        rollingFileAppender.setMaxFileSize("10MB");
        ConsoleAppender consoleAppender = new ConsoleAppender(new Layout() {
            @Override
            public String format(LoggingEvent loggingEvent) {
                SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
                StringBuilder extraData = new StringBuilder();
                String color;
                ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();

                switch (loggingEvent.getLevel().toInt()) {
                    case Level.WARN_INT:
                        color = ConsoleColors.RED_BRIGHT;
                        break;
                    case Level.ERROR_INT:
                        color = ConsoleColors.RED_BOLD + ConsoleColors.RED_UNDERLINED;
                        break;
                    case Level.FATAL_INT:
                        color = ConsoleColors.BLACK;
                        break;
                    default:
                        color = "";
                        break;
                }

                if (throwableInformation != null) {
                    extraData.append(ConsoleColors.RED);
                    Arrays.asList(throwableInformation.getThrowable().getStackTrace()).forEach(stackTraceElement -> extraData.append(stackTraceElement).append("\n"));
                }
                return color + "[" + date.format(loggingEvent.getTimeStamp()) + " - Server] " + loggingEvent.getLevel() + ": " + loggingEvent.getMessage() + "\n" + extraData.toString() + ConsoleColors.RESET;
            }

            @Override
            public boolean ignoresThrowable() {
                return false;
            }

            @Override
            public void activateOptions() {

            }
        });

        logger.addAppender(rollingFileAppender);
        logger.addAppender(consoleAppender);

        return logger;
    }

}
