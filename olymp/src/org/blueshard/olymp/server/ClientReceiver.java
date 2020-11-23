package org.blueshard.olymp.server;

import org.apache.log4j.Logger;
import org.blueshard.olymp.data.DataCodes;
import org.blueshard.olymp.data.Data;
import org.blueshard.olymp.e2ee.E2EEConverter;
import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.user.User;

import java.io.*;
import java.security.GeneralSecurityException;

public class ClientReceiver {

    private final User user;
    private final ClientFactory.Client client;
    private final byte[] clientPublicKey;
    private final byte[] privateKey;
    private final Logger logger;

    private final DataInputStream clientInput;
    private final ClientSender clientSender;

    ClientReceiver(User user, ClientFactory.Client client, ClientSender clientSender, byte[] clientPublicKey, byte[] privateKey, Logger logger) throws IOException {
        this.user = user;
        this.client = client;
        this.clientPublicKey = clientPublicKey;
        this.privateKey = privateKey;
        this.logger = logger;

        try {
            this.clientInput = new DataInputStream(this.client.getInputStream());
            this.clientSender = clientSender;
        } catch (IOException e) {
            throw new IOException(ResultCodes.UNEXPECTEDEXIT + ";" + e.getMessage());
        }
    }

    public int main() {
        Data inputData;
        Action action = new Action(null, clientSender);

        while (true) {
            try {
                inputData = new Data(E2EEConverter.decrypt(clientInput.readUTF(), privateKey));
            } catch (IOException | GeneralSecurityException e) {
                return ResultCodes.UNEXPECTEDEXIT;
            } catch (UnexpectedException e) {
                clientSender.unexpectedError();
                return ResultCodes.UNEXPECTEDEXIT;
            }
            action.refreshData(inputData);
            switch (inputData.getCode()) {
                case DataCodes.Client.UNEXPECTEDEXIT:
                    return ResultCodes.UNEXPECTEDEXIT;

                case DataCodes.Client.CLOSE:
                    return ResultCodes.EXIT;

                case DataCodes.Client.GETFILESDATA:
                    String startDirectory = inputData.getFromData(DataCodes.Params.File.STARTDIRECOTRY);
                    if (startDirectory == null) {
                        startDirectory = "/";
                    }
                    action.sendUserFilesData(user.getUUID(), startDirectory);
                    break;

                case DataCodes.Client.GETFILE:
                    action.sendFile(user.getUserfileDirectory() + inputData.getFromData(DataCodes.Params.File.FILEPATH), privateKey);
                    break;

                case DataCodes.Client.SENDFILE:
                    action.receiveFile(user, clientPublicKey);
                    break;
            }
        }
    }

    public static class ResultCodes {

        public static final int EXIT = 25;
        public static final int UNEXPECTEDEXIT = 84;

    }

}
