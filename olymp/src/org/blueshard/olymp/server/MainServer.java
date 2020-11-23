package org.blueshard.olymp.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.blueshard.olymp.Main;
import org.blueshard.olymp.data.Data;
import org.blueshard.olymp.data.DataCodes;
import org.blueshard.olymp.e2ee.E2EEConverter;
import org.blueshard.olymp.exception.*;
import org.blueshard.olymp.logging.ClientLogger;
import org.blueshard.olymp.register_code.RegisterCode;
import org.blueshard.olymp.user.User;
import org.blueshard.olymp.utils.ExceptionUtils;
import org.blueshard.olymp.version.TheosUIVersion;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.concurrent.Executors;

public class MainServer {

    private final int port;
    private final Logger logger;

    private ServerSocket serverSocket = null;
    private ClientFactory clientFactory = new ClientFactory();

    public MainServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    public void start() {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        } catch (IOException e) {
            logger.fatal("Couldn't create server");
            e.printStackTrace();
            return;
        }
        logger.info("Start server");

        logger.info("Server infos: " + serverSocket.toString());

        while (true){
            ClientFactory.Client client;
            try {
                client = clientFactory.createNewClient(serverSocket.accept(), null);
            } catch (IOException e) {
                logger.error("Couldn't connect to new client");
                e.printStackTrace();
                return;
            }

            logger.info("New Client connected - ID: " + client.getId());
            try {
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        boolean notLoggedIn = true;
                        Data inputData;
                        Logger clientLogger = null;
                        User user = null;

                        byte[] clientPublicKey;

                        if (client.getInetAddress().getHostAddress().equals("127.0.0.1")) {
                            logger.info("Localhost connected");
                            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                            String connectionType = dataInputStream.readUTF().strip().toLowerCase();
                            switch (connectionType) {
                                case "sql":
                                    logger.info("Localhost acts in SQL mode");
                                    Connection connection = Main.getConnection();
                                    StringBuilder resultData = new StringBuilder();
                                    ResultSetMetaData resultSetMetaData;
                                    String input;

                                    while (true) {
                                        try {
                                            input = dataInputStream.readUTF().replaceAll("\\s{2,}", " ").strip();
                                            if (input.toLowerCase().equals("exit")) {
                                                logger.info("Localhost exit sql mode");
                                                break;
                                            } else if (input.toLowerCase().startsWith("add register code")) {
                                                String times = input.toLowerCase().substring("add register code".length() - 1);
                                                if (times.isBlank()) {
                                                    String registerCode = RegisterCode.generateNewRegisterCode();
                                                    logger.info("Localhost generated a new register code ('" + registerCode + "')");
                                                    dataOutputStream.writeUTF(registerCode);
                                                } else {
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    int intTimes;
                                                    try {
                                                        intTimes = Integer.parseInt(times);
                                                    } catch (NumberFormatException e) {
                                                        logger.warn("'" + times + "' is not a valid number", e);
                                                        dataOutputStream.writeUTF(ExceptionUtils.extractExceptionMessage(e));
                                                        continue;
                                                    }
                                                    for (int i = 1; i < intTimes + 1; i++) {
                                                        stringBuilder.append(RegisterCode.generateNewRegisterCode()).append("\n");
                                                    }
                                                    logger.info("Localhost generated a new register codes ('" + stringBuilder.toString() + "')");
                                                    dataOutputStream.writeUTF(stringBuilder.toString());
                                                }
                                            } else if (input.toLowerCase().startsWith("all register codes")) {
                                                HashSet<String> allRegisterCodes = RegisterCode.getAllRegisterCodes();
                                                logger.info("Localhost got all register codes");
                                                dataOutputStream.writeUTF(Arrays.toString(allRegisterCodes.toArray(new String[allRegisterCodes.size()])));
                                            } else if (input.toLowerCase().startsWith("is register code")) {
                                                String registerCode = input.toLowerCase().substring("is register code".length() - 1);
                                                boolean isRegisterCode = RegisterCode.isRegisterCode(registerCode);
                                                logger.info("Localhost checked register code '" + registerCode + "' for it's validity (" + isRegisterCode + ")");
                                                dataOutputStream.writeUTF(String.valueOf(isRegisterCode));
                                            } else if (input.toLowerCase().startsWith("remove register code")) {
                                                String registerCode = input.toLowerCase().substring("remove register code".length() - 1);
                                                RegisterCode.removeRegisterCode(registerCode);
                                                logger.info("Localhost removed register code '" + registerCode + "'");
                                            } else {
                                                logger.info("Localhost send following SQL query: '" + input + "'");
                                                try {
                                                    ResultSet resultSet = connection.createStatement().executeQuery(input);
                                                    logger.info("SQL query from localhost executed successfully");
                                                    while (resultSet.next()) {
                                                        resultSetMetaData = resultSet.getMetaData();
                                                        for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; i++) {
                                                            resultData.append(resultSetMetaData.getColumnName(i)).append(resultSet.getString(i)).append("\n");
                                                        }
                                                    }
                                                    dataOutputStream.writeUTF(resultData.toString());
                                                    logger.info("Sent SQL query result set");
                                                } catch (SQLException e) {
                                                    logger.warn("SQL query from localhost caused an error", e);
                                                    dataOutputStream.writeUTF(e.getSQLState());
                                                }
                                                resultData.delete(0, resultData.length());
                                            }
                                        } catch (FatalIOException e) {
                                            logger.fatal("Fatal IO Exception occurred (" + ExceptionUtils.errorNumberToString(e.getErrno()) + ")", e);
                                            dataOutputStream.writeUTF("Fatal IO Exception occurred (" + ExceptionUtils.errorNumberToString(e.getErrno()) + ")\n" + ExceptionUtils.extractExceptionMessage(e));
                                        }
                                    }
                                    break;
                            }
                            client.close();
                            logger.info("Localhost disconnected");
                            return;
                        }

                        while (true) {
                            String clientData = new DataInputStream(client.getInputStream()).readUTF();

                            if (Data.isDataString(clientData)) {
                                if (new Data(clientData).getCode() == DataCodes.Client.PUBLICKEY) {
                                    clientPublicKey = Base64.getDecoder().decode(new Data(clientData).getFromData(DataCodes.Params.Key.PUBLICKEY));
                                    logger.info("Received client public key");
                                    break;
                                } else {
                                    logger.info("Client send invalid data");
                                }
                            } else {
                                logger.info("Client send invalid data");
                            }
                        }

                        byte[] privateKey = Action.publicKey(client.getOutputStream(), logger).getEncoded();

                        ClientSender clientSender;
                        try {
                            clientSender = new ClientSender(client, clientPublicKey, logger);
                        } catch (GeneralSecurityException ignore) {
                            return;
                        }
                        Action action = new Action(null, clientSender);

                        String agent = null;
                        String update = null;
                        TheosUIVersion version = null;

                        try {
                            Data data = new Data(E2EEConverter.decrypt(new DataInputStream(client.getInputStream()).readUTF(), privateKey));
                            String clientAgent = data.getFromData(DataCodes.Params.ClientAgent.CLIENTAGENT);

                            if (clientAgent.equals(DataCodes.Params.ClientAgent.THEOSUI)) {
                                client.setClientAgent(clientAgent);
                                agent = DataCodes.Params.ClientAgent.THEOSUI;
                                version = new TheosUIVersion(data.getFromData(DataCodes.Params.Version.VERSION));

                                if (version.hasRequiredUpdate()) {
                                    logger.info("Client has a deprecated TheosUI version");
                                    update = "r";
                                } else if (version.hasOptionalUpdate()) {
                                    logger.info("Client has a deprecated TheosUI version");
                                    update = "o";
                                }
                            } else {
                                logger.error("Client use an invalid client agent, connection get closed");
                            }
                        } catch (GeneralSecurityException | IllegalAccessException ignore) {
                        }

                        action.firstConnectionResult(agent != null, update != null);

                        if (agent == null) {
                            clientSender.close();
                            client.close();
                            return;
                        }

                        if (update != null && agent.equals(DataCodes.Params.ClientAgent.THEOSUI)) {
                            if (update.equals("r")) {
                                action.requestArtificeUIRequiredUpdate(version);
                            } else if (update.equals("o")) {
                                action.requestArtificeUIOptionalUpdate(version);
                            }
                        }

                        while (notLoggedIn) {
                            try {
                                inputData = new Data(E2EEConverter.decrypt(new DataInputStream(client.getInputStream()).readUTF(), privateKey));
                            } catch (IOException | GeneralSecurityException e) {
                                clientSender.unexpectedError();
                                return;
                            }
                            action.refreshData(inputData);

                            switch (inputData.getCode()) {
                                case DataCodes.Client.CLOSE:
                                case DataCodes.Client.UNEXPECTEDEXIT:
                                    return;

                                case DataCodes.Client.REGISTERCODE:
                                    action.isRegisterCode();
                                    break;

                                case DataCodes.Client.LOGIN:
                                    try {
                                        user = action.login();
                                        if (String.valueOf(user.getLogLevel()).equals(DataCodes.Params.LogLevel.ALL)) {
                                            clientLogger = new ClientLogger(client, Level.ALL).getLogger();
                                        } else if (String.valueOf(user.getLogLevel()).equals(DataCodes.Params.LogLevel.WARNING)) {
                                            clientLogger = new ClientLogger(client, Level.WARN).getLogger();
                                        } else {
                                            clientLogger = new ClientLogger(client, Level.OFF).getLogger();
                                        }
                                        logger.info("New Client details - IP: " + client.getIPAddress() + "\n" +
                                                "Port: " + client.getPort());

                                        logger.info("Logged in successfully");
                                        notLoggedIn = false;
                                    } catch (IOException | UserNotExistException | IllegalPasswordException e) {
                                        clientSender.loginFailed();
                                        logger.warn("Login failed", e);
                                    } catch (IllegalCodeException e) {
                                        clientSender.unexpectedError();
                                        logger.warn("An unexpected error occurred", e);
                                    }
                                    break;

                                case DataCodes.Client.REGISTER:
                                    try {
                                        user = action.register();
                                        clientLogger = new ClientLogger(client, Level.OFF).getLogger();
                                        logger.info("Registered in successfully");
                                        notLoggedIn = false;
                                    } catch (IOException e) {
                                        clientSender.registerFailed();
                                        logger.warn("Register failed", e);
                                    } catch (UserAlreadyExistException e) {
                                        clientSender.registerFailed_UserExist();
                                        logger.warn("Couldn't register, user already exist", e);
                                    } catch (RegisterCodeNotExistException e) {
                                        clientSender.registerCodeNotExist();
                                        logger.warn("Couldn't register, given register code do not exist");
                                    }
                                    break;
                                default:
                                    clientSender.userNotLoggedIn();
                                    logger.warn("User isn't logged in");
                                    break;
                            }
                        }
                        int resultCode = new ClientReceiver(user, client, clientSender, clientPublicKey, privateKey, clientLogger).main();
                        if (resultCode == ClientReceiver.ResultCodes.EXIT) {
                            client.close();
                            logger.info("Closed connection to client");
                        } else if (resultCode == ClientReceiver.ResultCodes.UNEXPECTEDEXIT) {
                            client.close();
                            logger.warn("Unexpected closed the connection to client");
                        }

                    } catch (SocketException e) {
                        logger.error("Socket Exception for client " + client.getId() + " occurred", e);
                    } catch (IOException e) {
                        logger.error("IO Exception for client " + client.getId() +" occurred", e);
                    } catch (FatalIOException e) {
                        String name;
                        int errno;
                        ErrorCodes errorCodes = new ErrorCodes();
                        Field[] fields = ErrorCodes.class.getFields();

                        for (Field field : fields) {
                            try {
                                name = field.getName();
                                errno = (int) field.get(errorCodes);
                                if (errno == e.getErrno()) {
                                    logger.fatal("Fatal IOException occurred while login or register - Errno: " + e.getErrno() + " (" + name + ")", e);
                                    return;
                                }
                            } catch (IllegalAccessException illegalAccessException) {
                                illegalAccessException.printStackTrace();
                            }
                        }
                        logger.error("Fatal IOException occurred while login or register - Errno: " + e.getErrno(), e);
                    } catch (UnexpectedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                logger.error("An unexpected error occurred: " + stringWriter, e);
            }
        }

    }

    public void close() throws IOException {
        serverSocket.close();
        logger.info("Closed server");
    }

}
