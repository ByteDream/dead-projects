package org.blueshard.olymp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class ClientFactory {

    private TreeMap<Integer, Client> ids = new TreeMap<>();

    public Client createNewClient(Socket socket, String clientAgent) {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(1, 999);
        } while (ids.containsKey(id));

        return new Client(id, socket, clientAgent);
    }

    public class Client {

        private final int id;
        private final Socket socket;
        private String clientAgent;

        public Client(int id, Socket socket, String clientAgent) {
            ClientFactory.this.ids.clear();
            this.id = id;
            this.socket = socket;
            this.clientAgent = clientAgent;
        }

        public void close() throws IOException {
            try {
                socket.close();
                ids.remove(id);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Couldn't close connection");
            }
        }

        public int getId() {
            return id;
        }

        public String getClientAgent() {
            return clientAgent;
        }

        public InetAddress getInetAddress() {
            return socket.getInetAddress();
        }

        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        public String getIPAddress() {
            return getInetAddress().getHostAddress();
        }

        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        public int getPort() {
            return socket.getPort();
        }

        public void setClientAgent(String clientAgent) throws IllegalAccessException {
            if (this.clientAgent != null) {
                throw new IllegalAccessException("Client agent is already set");
            } else {
                this.clientAgent = clientAgent;
            }
        }

    }

}
