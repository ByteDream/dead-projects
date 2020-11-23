package org.blueshard.sekaijuclt.client;

import org.apache.log4j.Logger;
import org.blueshard.sekaijuclt.data.DataCodes;
import org.blueshard.sekaijuclt.exception.IllegalCodeException;
import org.blueshard.sekaijuserv.data.Data;
import org.blueshard.sekaijuserv.exception.UnexpectedException;
import org.blueshard.sekaijuserv.server.ClientSender;
import org.blueshard.sekaijuserv.user.UserParams;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class MainClient {

    private final int serverPort;
    private final String serverIP;
    private final Logger logger;

    public MainClient(int serverPort, String serverIP, Logger logger) {
        this.serverPort = serverPort;
        this.serverIP = serverIP;
        this.logger = logger;
    }

    public void start(String trustStoreFile, String trustStorePassword) throws IOException {
        System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        SSLSocket server = (SSLSocket) SSLSocketFactory.getDefault().createSocket("192.168", 8269);

        byte[] privateKey;
        try {
            privateKey = new org.blueshard.sekaijuserv.server.Action(null, new ClientSender(server, new byte[0], logger)).publicKey().getEncoded();
        } catch (GeneralSecurityException | UnexpectedException ignore) {
            return;
        }
        byte[] serverPublicKey = Base64.getDecoder().decode(new Data(new DataInputStream(server.getInputStream()).readUTF()).getFromData(UserParams.Key.PUBLICKEY));

        ServerSender serverSender;
        try {
            serverSender = new ServerSender(server.getOutputStream(), serverPublicKey, logger);
        } catch (GeneralSecurityException | UnexpectedException ignore) {
            return;
        }
        Action action = new Action(null, serverSender);

        logger.info("Started client");

        Console console = System.console();

        while (true) {
            String logReg = console.readLine("To connect type 'login' to login, or 'register' to register: ").strip().toLowerCase();
            if (logReg.equals("login")) {
                String username = console.readLine("Username: ").strip();
                String password = String.valueOf(console.readPassword("Password: ")).strip();

                int resultCode = action.login(username, password);

                if (resultCode == DataCodes.Server.LOGINFAIL) {
                    logger.warn("Login failed");
                } else if (resultCode == DataCodes.Server.LOGINSUCCESS) {
                    logger.info("Logged in successfully");
                    break;
                } else if (resultCode == DataCodes.Server.UNEXPECTEDERROR) {
                    logger.warn("An unexpected error occurred");
                } else if (resultCode == DataCodes.Server.UNEXPECTEDEXIT) {
                    logger.fatal("Server exited unexpected");
                    return;
                }
            } else if (logReg.equals("register")) {
                String password;
                String passwordAgain;

                String username = console.readLine("Username: ");
                do {
                    password = String.valueOf(console.readPassword("Password: "));
                    passwordAgain = String.valueOf(console.readPassword("Re-type your Password: "));
                } while (!password.equals(passwordAgain));
                String email = console.readLine("Email: ");

                int resultCode = action.register(username, password, email);

                if (resultCode == DataCodes.Server.REGISTERFAIL) {
                    logger.warn("Register failed");
                } else if (resultCode == DataCodes.Server.REGISTERFAIL_USER_EXIST) {
                    logger.warn("The user already exists");
                } else if (resultCode == DataCodes.Server.REGISTERSUCCESS) {
                    logger.info("Registered successfully");
                    break;
                } else if (resultCode == DataCodes.Server.UNEXPECTEDERROR) {
                    logger.warn("An unexpected error occurred");
                } else if (resultCode == DataCodes.Server.UNEXPECTEDEXIT) {
                    logger.fatal("Server exited unexpected");
                    return;
                }
            }
        }

        ServerSendRecv sendRecv = new ServerSendRecv(server, logger);
        try {
            sendRecv.main();
        } catch (IllegalCodeException e) {
            serverSender.unexpectedExit();
        }
    }
}
