package org.blueshard.theosUI.files;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class ConfReader {

    public static class SingleConfReader {

        private File confFile;
        private TreeMap<String, String> entries = new TreeMap<>();

        public SingleConfReader(File confFile) throws IOException {
            initialize(confFile);
        }

        public SingleConfReader(String confFile) throws IOException {
            initialize(new File(confFile));
        }

        private void initialize(File confFile) throws IOException {
            this.confFile = confFile;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(confFile));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.trim().isEmpty() || line.isEmpty()) {
                    entries.put("#", null);
                } else if (line.contains("#")) {
                    entries.put("#", line.substring(1));
                } else {
                    String[] keyValue = line.split("=", 1);

                    entries.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }

            bufferedReader.close();
        }

        public boolean containsKey(String sectionName, String key) {
            return entries.containsKey(key);
        }

        public boolean containsValue(String sectionName, String value) {
            return entries.containsKey(value);
        }

        public TreeMap<String, String> getAll() {
            return entries;
        }

        public String getKey(String value) {
            for (Map.Entry<String, String> entry: entries.entrySet()) {
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }

            return null;
        }

        public String getValue(String key) {
            return entries.get(key);
        }

        public void remove(String key) {
            entries.remove(key);
        }

        public void replace(String key, String newValue) {
            entries.replace(key, newValue);
        }

    }

    public static class MultipleConfReader {

        private File confFile;
        private TreeMap<String, TreeMap<String, String>> entries = new TreeMap<>();

        public MultipleConfReader(File confFile) throws IOException {
            initialize(confFile);
        }

        public MultipleConfReader(String confFile) throws IOException {
            initialize(new File(confFile));
        }

        private void initialize(File confFile) throws IOException {
            this.confFile = confFile;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(confFile));
            String currentSection = null;
            TreeMap<String, String> currentEntries = new TreeMap<>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.trim().isEmpty() || line.isEmpty()) {
                    currentEntries.put("#", null);
                } else if (line.contains("#")) {
                    currentEntries.put("#", line.substring(1));
                } else if (line.startsWith("[") && line.endsWith("]")) {
                    if (currentSection != null) {
                        entries.put(currentSection, currentEntries);
                    }
                    currentSection = line.substring(1, line.length() - 1);
                } else {
                    String[] keyValue = line.split("=", 1);
                    if (keyValue.length == 1) {
                        currentEntries.put(keyValue[0].trim(), "");
                    } else {
                        currentEntries.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }

            bufferedReader.close();
        }

        public boolean containsKey(String sectionName, String key) {
            return entries.get(sectionName).containsKey(key);
        }

        public boolean containsValue(String sectionName, String value) {
            return entries.get(sectionName).containsKey(value);
        }

        public boolean containsSection(String sectionName) {
            return entries.containsKey(sectionName);
        }

        public TreeMap<String, TreeMap<String, String>> getAll() {
            return entries;
        }

        public String getKey(String sectionName, String value) {
            for (Map.Entry<String, String> entry: entries.get(sectionName).entrySet()) {
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }

            return null;
        }

        public String getValue(String sectionName, String key) {
            return entries.get(sectionName).get(key);
        }

        public void remove(String sectionName, String key) {
            entries.remove(sectionName).remove(key);
        }

        public void removeSection(String sectionName) {
            entries.remove(sectionName);
        }

        public void replace(String sectionName, String key, String newValue) {
            entries.get(sectionName).replace(key, newValue);
        }

    }

}
