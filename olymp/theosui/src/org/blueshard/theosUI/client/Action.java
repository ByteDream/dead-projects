package org.blueshard.theosUI.client;

import org.apache.log4j.Logger;
import org.blueshard.theosUI.data.Data;
import org.blueshard.theosUI.data.DataCodes;
import org.blueshard.theosUI.e2ee.E2EEConverter;
import org.blueshard.theosUI.e2ee.RSACipher;
import org.blueshard.theosUI.exception.ErrorCodes;
import org.blueshard.theosUI.exception.FatalIOException;
import org.blueshard.theosUI.exception.IllegalCodeException;
import org.blueshard.theosUI.exception.UnexpectedException;
import org.blueshard.theosUI.security.Password;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.*;


public class Action {

    private final DataInputStream dataInputStream;
    private final byte[] privateKey;
    private final ServerSender serverSender;

    public Action(InputStream inputStream, byte[] privateKey, ServerSender serverSender) {
        this.dataInputStream = new DataInputStream(inputStream);
        this.privateKey = privateKey;
        this.serverSender = serverSender;
    }

    public Data decryptE2EEData() throws IOException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, UnexpectedException, InvalidKeyException, InvalidKeySpecException {
        return new Data(E2EEConverter.decrypt(dataInputStream.readUTF(), privateKey));
    }

    public void closeConnection() {
        serverSender.close();
    }

    public int login(String username, String clearPassword) throws IOException, InvalidKeySpecException, IllegalBlockSizeException, UnexpectedException, NoSuchPaddingException, InvalidKeyException, BadPaddingException {
        Password.PasswordInfos passwordInfos = Password.createPassword(clearPassword, new byte[64]);
        serverSender.login(username, passwordInfos);
        return decryptE2EEData().getCode();
    }

    public boolean registerCode(String registerCode) throws IOException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, UnexpectedException, InvalidKeyException, InvalidKeySpecException {
        serverSender.isRegisterCode(registerCode);
        return Boolean.parseBoolean(decryptE2EEData().getFromData(DataCodes.Params.RegisterCode.REGISTERCODE));
    }

    public int register(String username, String clearPassword, String email) throws IOException, InvalidKeySpecException, IllegalBlockSizeException, UnexpectedException, NoSuchPaddingException, InvalidKeyException, BadPaddingException {
        Password.PasswordInfos passwordInfos = Password.createPassword(clearPassword, new byte[64]);
        serverSender.register(username, passwordInfos, email);
        return decryptE2EEData().getCode();
    }

    public Object[] getFilesData(String directory) throws IOException, IllegalCodeException, BadPaddingException, IllegalBlockSizeException, UnexpectedException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        serverSender.getFilesData(directory);
        Data data = decryptE2EEData();
        HashSet<String> directories = new HashSet<>();
        HashMap<String, String> files = new HashMap<>();
        if (data.getCode() != DataCodes.Server.SENDFILESDATA) {
            throw new IllegalCodeException(data.getCode(), DataCodes.Server.SENDFILESDATA);
        }
        data.getData().forEach((index, file) -> {
            if (index.startsWith("d")) {
                directories.add(file);
            } else {
                files.put(file, index.substring(1));
            }
        });

        Object[] returnObjects = new Object[2];
        returnObjects[0] = directories;
        returnObjects[1] = files;

        return returnObjects;
    }

    public static Object[] initE2EE(InputStream inputStream, OutputStream outputStream, Logger logger) throws UnexpectedException, IOException {
        Object[] returnArray = new Object[2];
        KeyPair keyPair = RSACipher.generateKeyPair();
        returnArray[0] = keyPair.getPrivate().getEncoded();

        ServerSender.publicKey(keyPair.getPublic(), new DataOutputStream(outputStream), logger);
        returnArray[1] = Base64.getDecoder().decode(new Data(new DataInputStream(inputStream).readUTF()).getFromData(DataCodes.Params.Key.PUBLICKEY));


        return returnArray;
    }

    public boolean initConnection() throws BadPaddingException, InvalidKeySpecException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, UnexpectedException, FatalIOException {
        serverSender.firstConnect();
        Data result = decryptE2EEData();

        if (!Boolean.parseBoolean(result.getFromData(DataCodes.Params.ClientAgent.VALIDAGENT))) {
            throw new FatalIOException(ErrorCodes.invalid_client_agent, "Using an invalid client agent"); // this should never get thrown
        } else return Boolean.parseBoolean(result.getFromData(DataCodes.Params.Update.UPDATE));
    }

}
