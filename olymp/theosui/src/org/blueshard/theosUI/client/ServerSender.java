package org.blueshard.theosUI.client;

import org.apache.log4j.Logger;
import org.blueshard.theosUI.Main;
import org.blueshard.theosUI.data.Data;
import org.blueshard.theosUI.data.DataCodes;
import org.blueshard.theosUI.e2ee.E2EEConverter;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.security.Password;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

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

    public void close() {
        try {
            send(new Data.Builder(DataCodes.Client.CLOSE));
            logger.info(loggerInfo("close"));
        } catch (IOException e) {
            logger.warn(loggerWarning("close"));
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
            logger.info(loggerInfo("unexpected close"));
        } catch (IOException e) {
            logger.warn(loggerWarning("unexpected close"), e);
        }
    }

    public void firstConnect() {
        Data.Builder data = new Data.Builder(DataCodes.Client.FIRSTCONNECT);
        data.addData(DataCodes.Params.ClientAgent.CLIENTAGENT, DataCodes.Params.ClientAgent.TheosUI);
        data.addData(DataCodes.Params.Version.VERSION, Main.version);

        try {
            send(data);
            logger.info(loggerInfo("first connect"));
        } catch (IOException e) {
            logger.warn(loggerWarning("first connect"), e);
        }
    }

    //----- login / register -----//

    public void login(String username, Password.PasswordInfos passwordInfos) {
        Data.Builder data = new Data.Builder(DataCodes.Client.LOGIN);
        data.addData(DataCodes.Params.LogReg.USERNAME, username);
        data.addData(DataCodes.Params.LogReg.PASSWORD, passwordInfos.getPasswordAsString());
        data.addData(DataCodes.Params.LogReg.SALT, passwordInfos.getSaltAsString());

        try {
            send(data);
            logger.info(loggerInfo("login"));
        } catch (IOException e) {
            logger.warn(loggerWarning("login"), e);
        }
    }

    public void isRegisterCode(String registerCode) {
        Data.Builder data = new Data.Builder(DataCodes.Client.REGISTERCODE);
        data.addData(DataCodes.Params.RegisterCode.REGISTERCODE, registerCode);

        try {
            send(data);
            logger.info(loggerInfo("register code"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register code"), e);
        }
    }

    public void register(String username, Password.PasswordInfos passwordInfos, String email) {
        Data.Builder data = new Data.Builder(DataCodes.Client.REGISTER);
        data.addData(DataCodes.Params.LogReg.USERNAME, username);
        data.addData(DataCodes.Params.LogReg.PASSWORD, passwordInfos.getPasswordAsString());
        data.addData(DataCodes.Params.LogReg.SALT, passwordInfos.getSaltAsString());
        data.addData(DataCodes.Params.LogReg.EMAIL, email);

        try {
            send(data);
            logger.info(loggerInfo("register"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register"), e);
        }
    }

    //----- files -----//

    public void getFilesData(String directory) {
        Data.Builder data = new Data.Builder(DataCodes.Client.GETFILESDATA);

        data.addData(DataCodes.Params.File.STARTDIRECOTRY, directory);
        try {
            send(data);
            logger.info(loggerInfo("get files data"));
        } catch (IOException e) {
            logger.warn(loggerWarning("get files data"), e);
        }
    }

    public static void publicKey(PublicKey publicKey, OutputStream outputStream, Logger logger) {
        try {
            Data.Builder data = new Data.Builder(DataCodes.Client.PUBLICKEY);
            data.addData(DataCodes.Params.Key.PUBLICKEY, Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(data.getDataAsString() + "\n");
            dataOutputStream.flush();

            logger.info("Send public key data");
        } catch (IOException e) {
            logger.warn("Failed to send public key data");
        }
    }

}
