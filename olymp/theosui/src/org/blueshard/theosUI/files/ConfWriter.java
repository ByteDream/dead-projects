package org.blueshard.theosUI.files;

import java.io.*;
import java.util.*;

public class ConfWriter {


    public static class SingleConfWriter {

        private TreeMap<String, String> entries = new TreeMap<>();

        public void write(String filename) throws IOException {
            write(new File(filename));
        }

        public void write(File file) throws IOException {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String key;
            String value;

            for (Map.Entry<String, String> entry: entries.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();

                if (key.equals("#")) {
                    if (value == null) {
                        bufferedWriter.newLine();
                    } else {
                        bufferedWriter.write(key + value);
                    }
                } else {
                    bufferedWriter.write(key + " = " + value);
                }
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        }

        public void add(String key, String value) {
            if (key == null) {
                return;
            } else if (key.contains("#")) {
                throw new IllegalArgumentException("Found '#' in " + key);
            } else if (value.contains("#")) {
                throw new IllegalArgumentException("Found '#' in " + value);
            } else {
                entries.put(key, value);
            }
        }

        public void addAll(TreeMap<String, String> newEntries) {
            TreeMap<String, String> tempEntries = new TreeMap<>();

            newEntries.forEach((key, value) -> {
                if (key == null) {
                    return;
                } else if (key.contains("#")) {
                    throw new IllegalArgumentException("Found '#' in " + key);
                } else if (value.contains("#")) {
                    throw new IllegalArgumentException("Found '#' in " + value);
                } else {
                    tempEntries.put(key, value);
                }
            });

            entries.putAll(tempEntries);
        }

        public void addBlankLine() {
            TreeMap<String, String> newEntries = entries;

            newEntries.put("#", null);

            entries = newEntries;
        }

        public void addComment(String comment) {
            TreeMap<String, String> newEntries = entries;

            newEntries.put("#", comment);

            entries = newEntries;
        }

        public TreeMap<String, String> getAll() {
            return entries;
        }

        public void getAndAdd(String string) throws IOException {
            getAndAdd(new File(string));
        }

        public void getAndAdd(File file) throws IOException {
            entries.putAll(new ConfReader.SingleConfReader(file).getAll());
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

    public static class MultipleConfWriter {

        private TreeMap<String, TreeMap<String, String>> entries = new TreeMap<>();

        public void createNewSection(String sectionName) {
            entries.put(sectionName, new TreeMap<>());
        }

        public void write(String filename) throws IOException {
            write(new File(filename));
        }

        public void write(File file) throws IOException {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            String sectionKey;
            String sectionValue;

            for (Map.Entry<String, TreeMap<String, String>> entry: entries.entrySet()) {
                String sectionName = entry.getKey();

                bufferedWriter.write("[" + sectionName + "]");
                bufferedWriter.newLine();
                bufferedWriter.newLine();

                for (Map.Entry<String, String> sectionEntry: entries.get(sectionName).entrySet()) {
                    sectionKey = sectionEntry.getKey();
                    sectionValue = sectionEntry.getValue();

                    if (sectionKey.equals("#")) {
                        if (sectionValue == null) {
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.write(sectionKey + sectionValue);
                        }
                    } else {
                        bufferedWriter.write(sectionKey + " = " + sectionValue);
                    }
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        }

        public void add(String sectionName, String key, String value) {
            if (sectionName == null || key == null || !entries.containsKey(sectionName)) {
                return;
            } else if (key.contains("#")) {
                throw new IllegalArgumentException("Found '#' in " + key);
            } else if (value.contains("#")) {
                throw new IllegalArgumentException("Found '#' in " + value);
            } else {
                TreeMap<String, String> newEntries = entries.get(sectionName);
                newEntries.put(key, value);
                entries.replace(sectionName, new TreeMap<>(newEntries));
            }
        }

        public void addAll(String sectionName, TreeMap<String, String> newEntries) {
            TreeMap<String, String> tempEntries = new TreeMap<>();

            if (sectionName == null || !newEntries.containsKey(sectionName)) {
                return;
            } else {
                newEntries.forEach((key, value) -> {
                    if (key == null) {
                        return;
                    } else if (key.contains("#")) {
                        throw new IllegalArgumentException("Found '#' in " + key);
                    } else if (value.contains("#")) {
                        throw new IllegalArgumentException("Found '#' in " + value);
                    } else {
                        tempEntries.put(key, value);
                    }
                });
            }

            TreeMap<String, String> newSectionEntries = entries.get(sectionName);
            newSectionEntries.putAll(tempEntries);
            entries.replace(sectionName, new TreeMap<>(newSectionEntries));
        }

        public void addBlankLine(String sectionName) {
            TreeMap<String, String> newEntries = entries.get(sectionName);

            newEntries.put("#", null);

            entries.put(sectionName, newEntries);
        }

        public void addComment(String sectionName, String comment) {
            TreeMap<String, String> newEntries = entries.get(sectionName);

            newEntries.put("#", comment);

            entries.put(sectionName, newEntries);
        }

        public void addSingleConfWriter(String sectionName, SingleConfWriter singleConfWriter) {
            entries.put(sectionName, singleConfWriter.entries);
        }

        public TreeMap<String, TreeMap<String, String>> getAll() {
            return entries;
        }

        public void getAndAdd(String string) throws IOException {
            getAndAdd(new File(string));
        }

        public void getAndAdd(File file) throws IOException {
            entries.putAll(new ConfReader.MultipleConfReader(file).getAll());
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
