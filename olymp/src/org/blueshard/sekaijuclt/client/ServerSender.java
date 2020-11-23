package org.blueshard.sekaijuclt.client;

import org.apache.log4j.Logger;
import org.blueshard.sekaijuclt.data.Data;
import org.blueshard.sekaijuclt.data.DataCodes;
import org.blueshard.sekaijuclt.security.Password;
import org.blueshard.sekaijuclt.user.UserParams;
import org.blueshard.sekaijuserv.e2ee.E2EEConverter;
import org.blueshard.sekaijuserv.exception.UnexpectedException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

public class ServerSender {

    private final DataOutputStream dataOutputStream;
    private final byte[] serverPublicKey;
    private final Logger logger;

    public ServerSender(OutputStream outputStream, byte[] serverPublicKey, Logger logger) throws IllegalBlockSizeException, UnexpectedException, InvalidKeySpecException, InvalidKeyException, BadPaddingException {
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.serverPublicKey = serverPublicKey;
        this.logger = logger;

        if (serverPublicKey.length != 0) {
            E2EEConverter.encrypt("Test", serverPublicKey);  // just test if the server key works
        }
    }

    private void send(Data data) throws IOException {
        if (serverPublicKey.length == 0) {
            dataOutputStream.writeUTF(data.getDataAsString() + "\n");
        } else {
            try {
                dataOutputStream.writeUTF(E2EEConverter.encrypt(data.getDataAsString() + "\n", serverPublicKey));
            } catch (UnexpectedException e) {
                logger.error("An unexpected error occurred", e);
                unexpectedError();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ignore) {
            }
        }
        dataOutputStream.flush();
    }

    private void send(Data.Builder data) throws IOException {
        send(data.getData());
    }

    private String loggerInfo(String data) {
        return "Send " + data + " data";
    }

    private String loggerWarning(String data) {
        return "Failed to send " + data + " data";
    }

    public void exit() {
        try {
            send(new Data.Builder(DataCodes.Client.EXIT));
            logger.info(loggerInfo("exit"));
        } catch (IOException e) {
            logger.warn(loggerWarning("exit"));
        }
    }

    public void unexpectedError() {
        try {
            send(new Data.Builder(DataCodes.Client.UNEXPECTEDERROR));
            logger.info(loggerInfo("unexpected error"));
        } catch (IOException e) {
            logger.warn(loggerWarning("unexpected error"), e);
        }
    }

    public void unexpectedExit() {
        try {
            send(new Data.Builder(DataCodes.Client.UNEXPECTEDEXIT));
            logger.info(loggerInfo("unexpected exit"));
        } catch (IOException e) {
            logger.warn(loggerWarning("unexpected exit"), e);
        }
    }

    public void publicKey(String publicKey) {
        try {
            Data.Builder data = new Data.Builder(DataCodes.Client.PUBLICKEY);
            data.addData(org.blueshard.sekaijuserv.user.UserParams.Key.PUBLICKEY, publicKey);

            send(data);
            logger.info(loggerInfo("public key"));
        } catch (IOException e) {
            logger.warn(loggerWarning("public key"));
        }
    }

    //----- login / register -----//

    public void login(String username, Password.PasswordInfos passwordInfos) {
        Data.Builder data = new Data.Builder(DataCodes.Client.LOGIN);
        data.addData(UserParams.USERNAME, username);
        data.addData(UserParams.PASSWORD, passwordInfos.getPasswordAsString());
        data.addData(UserParams.SALT, passwordInfos.getSaltAsString());

        try {
            send(data);
            logger.info(loggerInfo("login"));
        } catch (IOException e) {
            logger.warn(loggerWarning("login"), e);
        }
    }

    public void register(String username, Password.PasswordInfos passwordInfos, String email) {
        Data.Builder data = new Data.Builder(DataCodes.Client.REGISTER);
        data.addData(UserParams.USERNAME, username);
        data.addData(UserParams.PASSWORD, passwordInfos.getPasswordAsString());
        data.addData(UserParams.SALT, passwordInfos.getSaltAsString());
        data.addData(UserParams.EMAIL, email);

        try {
            send(data);
            logger.info(loggerInfo("register"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register"), e);
        }
    }

    //----- files -----//

    public void getFilesData() {
        Data.Builder data = new Data.Builder(DataCodes.Client.GETFILESDATA);

        try {
            send(data);
            logger.info(loggerInfo("get files data"));
        } catch (IOException e) {
            logger.warn(loggerWarning("get files data"), e);
        }
    }

}
