package org.blueshard.sekaijuclt.client;

import org.apache.log4j.Logger;
import org.blueshard.sekaijuclt.exception.IllegalCodeException;

import javax.net.ssl.SSLSocket;
import java.io.Console;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ServerSendRecv {

    private final Logger logger;

    private final Action action;
    private final DataInputStream serverInput;
    private final ServerSender serverSender;

    public ServerSendRecv(SSLSocket socket, ServerSender serverSender, byte[] privateKey, Logger logger) throws IOException {
        this.logger = logger;

        try {
            this.serverInput = new DataInputStream(socket.getInputStream());
            this.serverSender = serverSender;
        } catch (IOException e) {
            throw new IOException(ResultCode.UNEXPECTEDEXIT + ";" + e.getMessage());
        }
        this.action = new Action(socket.getInputStream(), this.serverSender);
    }

    public int main() throws IOException, IllegalCodeException {
        Console console = System.console();
        String input;

        System.out.println("Type ... to ...: " +
                "   exit        Close connection to server" +
                "   file-mode   Enter file mode");
        while (true) {
            input = console.readLine("Action: ").strip().toLowerCase();

            if (input.equals("exit")) {
                serverSender.exit();
                return ResultCode.EXIT;
            } else if (input.equals("file-mode")) {
                while (true) {
                    SendRecvFiles files = new SendRecvFiles(action);
                    input = console.readLine(files.getCurrentDirectory() + ": ").strip().toLowerCase();

                    if (input.startsWith("cd")) {
                        files.cdDirectory(input.substring(2).strip());
                    } else if (input.equals("ls")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        files.listFilesAsList().forEach(s -> stringBuilder.append(s).append("   "));
                        System.out.println(stringBuilder.toString());
                    } else if (input.equals("exit")) {
                        return ResultCode.EXIT;
                    }
                }
            }
        }
    }

    public static class SendRecvFiles {

        private final Action action;

        private ArrayList<String> currentDirectory = new ArrayList<>();
        private HashMap<String, Object> files = new HashMap<>();

        public SendRecvFiles(Action action) throws IOException, IllegalCodeException {
            this.action = action;
            addToFiles(action.getFilesData(""));
        }

        private void addToFiles(String[][] data) {
            for (int i = 0; i < data[0].length; i++) {
                files.put(data[0][i], new HashMap<>());
            }
            for (int i = 0; i < data[1].length; i++) {
                files.put(data[1][i], null);
            }
        }

        private ArrayList<String> toSortedArrayList(String[] files) {
            Arrays.sort(files, String::compareToIgnoreCase);
            return new ArrayList<>(Arrays.asList(files));
        }

        public void cdDirectory(String directory) throws IOException, IllegalCodeException {
            directory = directory.strip();
            if (directory.equals("..")) {
                if (currentDirectory.isEmpty()) {
                    return;
                } else {
                    currentDirectory.remove(currentDirectory.size() - 1);
                }
            } else {
                if (listFiles().get(directory) != null) {
                    HashMap<String, Object> files = (HashMap<String, Object>) listFiles().get(directory);
                    if (files.isEmpty()) {
                        addToFiles(action.getFilesData(getCurrentDirectory() + "/"));
                    }
                    currentDirectory.add(directory);
                }
            }
        }

        public ArrayList<String> listFilesAsList() {
            HashMap<String, Object> currentFiles = new HashMap<>();
            for (String s: currentDirectory) {
                currentFiles = (HashMap<String, Object>) files.get(s);
            }
            return toSortedArrayList((String[]) currentFiles.keySet().toArray());
        }

        public HashMap<String, Object> listFiles() {
            HashMap<String, Object> currentFiles = new HashMap<>();
            for (String s: currentDirectory) {
                currentFiles = (HashMap<String, Object>) files.get(s);
            }
            return currentFiles;
        }

        public String getCurrentDirectory() {
            StringBuilder stringBuilder = new StringBuilder();
            currentDirectory.forEach(stringBuilder::append);
            return stringBuilder.toString();
        }

    }

    public static class ResultCode {

        public static final int EXIT = 25;
        public static final int UNEXPECTEDEXIT = 84;

    }

}
