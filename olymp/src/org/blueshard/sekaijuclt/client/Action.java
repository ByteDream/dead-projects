package org.blueshard.sekaijuclt.client;

import org.blueshard.sekaijuclt.data.Data;
import org.blueshard.sekaijuclt.data.DataCodes;
import org.blueshard.sekaijuclt.exception.IllegalCodeException;
import org.blueshard.sekaijuclt.security.Password;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Action {

    private final DataInputStream dataInputStream;
    private final ServerSender serverSender;

    public Action(InputStream inputStream, ServerSender serverSender) {
        this.dataInputStream = new DataInputStream(inputStream);
        this.serverSender = serverSender;
    }

    public int login(String username, String clearPassword) throws IOException {
        Password.PasswordInfos passwordInfos = Password.createPassword(clearPassword, new byte[64]);
        serverSender.login(username, passwordInfos);
        return new Data(dataInputStream.readUTF()).getCode();
    }

    public int register(String username, String clearPassword, String email) throws IOException {
        Password.PasswordInfos passwordInfos = Password.createPassword(clearPassword, new byte[64]);
        serverSender.register(username, passwordInfos, email);
        return new Data(dataInputStream.readUTF()).getCode();
    }

    public String[][] getFilesData(String directory) throws IOException, IllegalCodeException {
        serverSender.getFilesData();
        Data data = new Data(dataInputStream.readUTF());
        String[][] files = new String[2][data.getData().hashCode()];
        if (data.getCode() != DataCodes.Server.SENDFILESDATA) {
            throw new IllegalCodeException(data.getCode(), DataCodes.Server.SENDFILESDATA);
        }
        data.getData().forEach((index, file) -> {
            if (index.startsWith("d")) {
                files[0][Integer.parseInt(index.substring(1))] = file;
            } else {
                files[1][Integer.parseInt(index.substring(1))] = file;
            }
        });
        return files;
    }

}
