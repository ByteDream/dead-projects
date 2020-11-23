package org.blueshard.olymp.server;

import org.apache.log4j.Logger;
import org.blueshard.olymp.data.Data;
import org.blueshard.olymp.data.DataCodes;
import org.blueshard.olymp.e2ee.E2EEConverter;
import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.fileserver.FileReceiver;
import org.blueshard.olymp.fileserver.FileSender;
import org.blueshard.olymp.files.ServerFiles;
import org.blueshard.olymp.utils.SizeUnit;
import org.blueshard.olymp.version.TheosUIVersion;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.concurrent.Executors;

public class ClientSender {

    private final ClientFactory.Client client;
    private final byte[] clientPublicKey;
    private final Logger logger;

    private final DataOutputStream dataOutputStream;

    public ClientSender(ClientFactory.Client client, byte[] clientPublicKey, Logger logger) throws IOException, IllegalBlockSizeException, UnexpectedException, InvalidKeySpecException, InvalidKeyException, BadPaddingException {
        this.client = client;
        this.clientPublicKey = clientPublicKey;
        this.logger = logger;

        this.dataOutputStream = new DataOutputStream(this.client.getOutputStream());

        if (clientPublicKey.length != 0) {
            E2EEConverter.encrypt("Test", clientPublicKey);  // just test if the client key works
        }
    }

    public void send(Data data) throws IOException {
        if (clientPublicKey.length == 0) {
            dataOutputStream.writeUTF(data.getDataAsString() + "\n");
        } else {
            try {
                dataOutputStream.writeUTF(E2EEConverter.encrypt(data.getDataAsString() + "\n", clientPublicKey));
            } catch (UnexpectedException e) {
                logger.error("An unexpected error occurred", e);
                unexpectedError();
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ignore) {
            }
        }
        dataOutputStream.flush();
    }

    public void send(Data.Builder data) throws IOException {
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
            send(new Data.Builder(DataCodes.Server.CLOSE));
            logger.info(loggerInfo("close"));
        } catch (IOException e) {
            logger.warn(loggerWarning("close"), e);
        }
    }

    public void unexpectedError() {
        try {
            send(new Data.Builder(DataCodes.Server.UNEXPECTEDERROR));
            logger.info(loggerInfo("unexpected error"));
        } catch (IOException e) {
            logger.warn(loggerWarning("unexpected error"));
        }
    }

    public void unexpectedExit() {
        try {
            send(new Data.Builder(DataCodes.Server.UNEXPECTEDEXIT));
            logger.info(loggerInfo("unexpected exit"));
        } catch (IOException e) {
            logger.warn(loggerWarning("unexpected exit"));
        }
    }

    public static void publicKey(PublicKey publicKey, DataOutputStream dataOutputStream, Logger logger) {
        try {
            Data.Builder data = new Data.Builder(DataCodes.Server.PUBLICKEY);
            data.addData(DataCodes.Params.Key.PUBLICKEY, Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            dataOutputStream.writeUTF(data.getDataAsString() + "\n");
            dataOutputStream.flush();

            logger.info("Send public key data");
        } catch (IOException e) {
            logger.warn("Failed to send public key data");
        }
    }

    public static void ready(DataOutputStream dataOutputStream, Logger logger) {
        try {
            dataOutputStream.writeUTF(new Data.Builder(DataCodes.Server.READY).getDataAsString() + "\n");
            dataOutputStream.flush();

            logger.info("Send ready data");
        } catch (IOException e) {
            logger.warn("Failed to send ready data", e);
        }
    }

    public void userNotLoggedIn() {
        try {
            send(new Data.Builder(DataCodes.Server.NOTLOGGEDIN));
            logger.info(loggerInfo("user not logged in"));
        } catch (IOException e) {
            logger.warn(loggerWarning("user not logged in"));
        }
    }

    //----- update -----//

    public void firstConnectResult(boolean validAgent, boolean update) {
        Data.Builder data = new Data.Builder(DataCodes.Server.FIRSTCONNECT);
        data.addData(DataCodes.Params.ClientAgent.VALIDAGENT, String.valueOf(validAgent));
        data.addData(DataCodes.Params.Update.UPDATE, String.valueOf(update));

        try {
            send(data);
            logger.info(loggerInfo("first connect result"));
        } catch (IOException e) {
            logger.warn(loggerWarning("first connect result"), e);
        }
    }

    public void requestArtificeUIOptionalUpdate(TheosUIVersion version, String changes) {
        Data.Builder data = new Data.Builder(DataCodes.Server.OPTIONALUPDATE);
        data.addData(DataCodes.Params.Update.NEWVERSION, version.toString());
        data.addData(DataCodes.Params.Update.CHANGES, changes);

        try {
            send(data);
            logger.info(loggerInfo("request ArtificeUI optional update"));
        } catch (IOException e) {
            logger.warn(loggerWarning("request ArtificeUI optional update"));
        }
    }

    public void requestArtificeUIRequiredUpdate(TheosUIVersion version, String changes) {
        Data.Builder data = new Data.Builder(DataCodes.Server.REQUIREDUPDATE);
        data.addData(DataCodes.Params.Update.NEWVERSION, version.toString());
        data.addData(DataCodes.Params.Update.CHANGES, changes);

        try {
            send(data);
            logger.info(loggerInfo("request ArtificeUI required update"));
        } catch (IOException e) {
            logger.warn(loggerWarning("request ArtificeUI required update"));
        }
    }

    //----- login / register -----//

    public void loginFailed() {
        try {
            send(new Data.Builder(DataCodes.Server.LOGINFAIL));
            logger.info(loggerInfo("login fail"));
        } catch (IOException e) {
            logger.warn(loggerWarning("login fail"));
        }
    }

    public void loginSuccess() {
        try {
            send(new Data.Builder(DataCodes.Server.LOGINSUCCESS));
            logger.info(loggerInfo("login success"));
        } catch (IOException e) {
            logger.warn(loggerWarning("login success"));
        }
    }

    public void registerCodeExist() {
        try {
            send(new Data.Builder(DataCodes.Server.REGISTERCODE_EXIST));
            logger.info(loggerInfo("register code exist"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register code exist"));
        }
    }

    public void registerCodeNotExist() {
        try {
            send(new Data.Builder(DataCodes.Server.REGISTERCODE_NOT_EXIST));
            logger.info(loggerInfo("register code not exist"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register code not exist"));
        }
    }

    public void registerFailed() {
        try {
            send(new Data.Builder(DataCodes.Server.REGISTERFAIL));
            logger.info(loggerInfo("register fail"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register fail"));
        }
    }

    public void registerFailed_UserExist() {
        try {
            send(new Data.Builder(DataCodes.Server.REGISTERFAIL_USER_EXIST));
            logger.info(loggerInfo("register fail, user exist"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register fail, user exist"));
        }
    }

    public void registerSuccess() {
        try {
            send(new Data.Builder(DataCodes.Server.REGISTERSUCCESS));
            System.out.println(new Data.Builder(DataCodes.Server.REGISTERSUCCESS).getDataAsString());
            logger.info(loggerInfo("register success"));
        } catch (IOException e) {
            logger.warn(loggerWarning("register success"));
        }
    }


    //----- filesData ----//

    public void filesData(String UUID, File startDirectory) {
        Data.Builder data = new Data.Builder(DataCodes.Server.SENDFILESDATA);

        String file;
        File[] files = startDirectory.listFiles();
        int userDirectoryLength = new ServerFiles.user_files(UUID).user_files_dir.length();

        for (int i = 0; i < files.length; i++) {
            file = startDirectory.getAbsolutePath() + "/" + files[i].getName();

            if (files[i].isDirectory()) {
                data.addData("d" + i, file.substring(userDirectoryLength));
            } else {
                data.addData("f" + SizeUnit.BYTES(files[i].length()).toMegabyte() + "MB", file.substring(userDirectoryLength));
            }
        }

        try {
            send(data);
            logger.info("Send files data");
        } catch (IOException e) {
            logger.warn("Failed to send user files data");
        }
    }

    public void receiveFile(String file, byte[] privateKey) {
        FileReceiver fileReceiver = new FileReceiver(client, new File(file), privateKey, logger);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                try {
                    String md5CheckSum = fileReceiver.main();
                } catch (UnexpectedException e) {
                    logger.warn("An unexpected io exception occurred", e);
                } catch (IllegalBlockSizeException | InvalidKeyException | InvalidKeySpecException | BadPaddingException | NoSuchPaddingException e) {
                    logger.warn("An unexpected exception occurred");
                }
                logger.info("Received file " + file);
                receiveFileSuccess();
            } catch (IOException e) {
                logger.warn("Failed to receive file " + file);
                e.printStackTrace();
            }
        });
    }

    public void receiveFileFailed() {
        try {
            send(new Data.Builder(DataCodes.Server.RECEIVEFILEFAIL));
            logger.info(loggerInfo("receive file fail"));
        } catch (IOException e) {
            logger.warn(loggerWarning("receive file fail"));
        }
    }

    public void receiveFileSuccess() {
        try {
            send(new Data.Builder(DataCodes.Server.RECEIVEFILESUCCESS));
            logger.info(loggerInfo("receive file success"));
        } catch (IOException e) {
            logger.warn(loggerWarning("receive file success"));
        }
    }

    public void sendFile(File file, byte[] publicKey) {
        FileSender fileSender = new FileSender(client, file, publicKey, logger);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String md5CheckSum = null;
                try {
                    md5CheckSum = fileSender.main();
                } catch (UnexpectedException e) {
                    logger.warn("An unexpected io exception occurred", e);
                } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException | InvalidKeyException e) {
                    logger.warn("An unexpected exception occurred");
                }
                logger.info("Send file " + file);
                sendFileSuccess(md5CheckSum);
            } catch (IOException e) {
                logger.warn("Failed to send file " + file.getAbsolutePath());
                e.printStackTrace();
                sendFileFailed();
            }
        });
    }

    public void sendFileFailed() {
        try {
            send(new Data.Builder(DataCodes.Server.SENDFILEFAIL));
            logger.info(loggerInfo("send file data"));
        } catch (IOException e) {
            logger.warn(loggerWarning("send file fail"));
        }
    }

    public void sendFileSuccess(String checkSum) {
        try {
            Data.Builder data = new Data.Builder(DataCodes.Server.SENDFILESSUCCESS);
            data.addData(DataCodes.Params.CheckSum.MD5, checkSum);
            send(data);
            logger.info(loggerInfo("send file success"));
        } catch (IOException e) {
            logger.warn(loggerWarning("send file success"));
        }
    }
}
