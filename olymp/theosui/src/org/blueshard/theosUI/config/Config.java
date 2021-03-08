package org.blueshard.theosUI.config;

import org.apache.log4j.Level;
import org.blueshard.theosUI.files.ConfReader;
import org.blueshard.theosUI.files.ConfWriter;
import org.blueshard.theosUI.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class Config {

    private TreeMap<String, String> configElements;
    private final File configFile;

    public Config() throws IOException {
        configFile = OSUtils.getLogFile();

        if (!configFile.isFile()) {
            File theosFolder = new File(configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length() - configFile.getName().length()));
            if (!theosFolder.isDirectory()) {
                if (!theosFolder.mkdir()) {
                    throw new IOException("Couldn't create theos folder");
                }
            }
            if (!configFile.createNewFile()) {
                throw new IOException("Couldn't create config file");
            } else {
                configElements = new TreeMap<>();
                configElements.put(ConfigEntryKeys.LOGLEVEL, String.valueOf(Level.ALL.toInt()));
                configElements.put(ConfigEntryKeys.FILELOGGING, String.valueOf(true));
            }
        } else {
            configElements = new ConfReader.SingleConfReader(configFile).getAll();
        }
    }

    public boolean getFileLogging() {
        return Boolean.parseBoolean(configElements.get(ConfigEntryKeys.FILELOGGING));
    }

    public void setFileLogging(boolean fileLogging) {
        configElements.put(ConfigEntryKeys.FILELOGGING, String.valueOf(fileLogging));
    }

    public Level getLogLevel() {
        return Level.toLevel(configElements.get(ConfigEntryKeys.LOGLEVEL));
    }

    public void setLogLevel(Level logLevel) {
        configElements.replace(ConfigEntryKeys.LOGLEVEL, String.valueOf(logLevel.toInt()));
    }

    private void write() throws IOException {
        ConfWriter.SingleConfWriter config = new ConfWriter.SingleConfWriter();
        config.addAll(configElements);

    }

    private static class ConfigEntryKeys {

        public static final String FILELOGGING = "fileLogging";
        public static final String LOGLEVEL = "logLevel";
    }

}
