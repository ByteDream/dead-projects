package org.blueshard.olymp.server;

import org.apache.log4j.Logger;
import org.blueshard.olymp.Main;
import org.blueshard.olymp.data.DataCodes;
import org.blueshard.olymp.data.Data;
import org.blueshard.olymp.e2ee.RSACipher;
import org.blueshard.olymp.exception.*;
import org.blueshard.olymp.files.ServerFiles;
import org.blueshard.olymp.register_code.RegisterCode;
import org.blueshard.olymp.sql.SQL;
import org.blueshard.olymp.sql.SQLPosition;
import org.blueshard.olymp.user.User;
import org.blueshard.olymp.version.TheosUIVersion;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Action {

    private Data data;
    private final ClientSender clientSender;

    public Action(Data data, ClientSender clientSender) {
        this.data = data;
        this.clientSender = clientSender;
    }

    public void refreshData(Data data) {
        this.data = data;
    }

    public void close() {
        clientSender.close();
    }

    public void firstConnectionResult(boolean validAgent, boolean update) {
        clientSender.firstConnectResult(validAgent, update);
    }

    public void isRegisterCode() throws FatalIOException {
        if (RegisterCode.isRegisterCode(data.getFromData(DataCodes.Params.RegisterCode.REGISTERCODE))) {
            clientSender.registerCodeExist();
        } else {
            clientSender.registerCodeNotExist();
        }
    }

    public static PrivateKey publicKey(OutputStream outputStream, Logger logger) throws UnexpectedException {
        KeyPair keyPair = RSACipher.generateKeyPair();

        ClientSender.publicKey(keyPair.getPublic(), new DataOutputStream(outputStream), logger);

        return keyPair.getPrivate();
    }

    public static void ready(OutputStream outputStream, Logger logger) {
        ClientSender.ready(new DataOutputStream(outputStream), logger);
    }

    public User login() throws UserNotExistException, IllegalCodeException, IllegalPasswordException, FatalIOException {
        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("SELECT UUID FROM USERS WHERE USERNAME = ?");

            preparedStatement.setString(1, data.getFromData(DataCodes.Params.LogReg.USERNAME));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User(resultSet.getString(1));

                if (data.getCode() != DataCodes.Client.LOGIN) {
                    throw new IllegalCodeException(data.getCode(), DataCodes.Client.LOGIN);
                } else if (!data.getFromData(DataCodes.Params.LogReg.PASSWORD).equals(user.getPasswordInfos().getPasswordAsString())) {
                    throw new IllegalPasswordException("Wrong password is given");
                }

                clientSender.loginSuccess();

                resultSet.close();

                return user;
            } else {
                resultSet.close();
                throw new UserNotExistException(data.getFromData(DataCodes.Params.LogReg.USERNAME), "The user doesn't exist");
            }
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_get_user_information, "Couldn't get UUID for user with name " + data.getFromData(DataCodes.Params.LogReg.USERNAME), e);
        }
    }

    public User register() throws FatalIOException, UserAlreadyExistException, RegisterCodeNotExistException {
        User user = User.createNewUser(data.getFromData(DataCodes.Params.RegisterCode.REGISTERCODE),
                data.getFromData(DataCodes.Params.LogReg.USERNAME), data.getFromData(DataCodes.Params.LogReg.PASSWORD),
                data.getFromData(DataCodes.Params.LogReg.SALT), data.getFromData(DataCodes.Params.LogReg.EMAIL));

        clientSender.registerSuccess();

        return user;
    }

    public void receiveFile(User user, byte[] privateKey) {
        clientSender.receiveFile(new ServerFiles.user_files(user.getUUID()) + data.getFromData(DataCodes.Params.File.FILEPATH), privateKey);
    }

    public void requestArtificeUIOptionalUpdate(TheosUIVersion version) {
        clientSender.requestArtificeUIOptionalUpdate(version, version.getChanges());
    }

    public void requestArtificeUIRequiredUpdate(TheosUIVersion version) {
        clientSender.requestArtificeUIRequiredUpdate(version, version.getChanges());
    }

    public void sendFile(String file, byte[] publicKey) {
        clientSender.sendFile(new File(file), publicKey);
    }

    public void sendUpdateFile(byte[] publicKey) {
        clientSender.sendFile(new File(ServerFiles.etc.update.TheosUI_jar), publicKey);
    }

    public void sendUserFilesData(String UUID, String startDirectory) {
        clientSender.filesData(UUID, new File(new ServerFiles.user_files(UUID).user_files_dir + startDirectory));
    }

}
