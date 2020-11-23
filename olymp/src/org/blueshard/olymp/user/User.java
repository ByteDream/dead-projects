package org.blueshard.olymp.user;

import org.blueshard.olymp.Main;
import org.blueshard.olymp.exception.*;
import org.blueshard.olymp.files.ServerFiles;
import org.blueshard.olymp.register_code.RegisterCode;
import org.blueshard.olymp.security.Password;
import org.blueshard.olymp.sql.SQL;
import org.blueshard.olymp.sql.SQLPosition;
import org.blueshard.olymp.utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

public class User extends UserInfos {

    private Password.PasswordInfos passwordInfos;

    public User(String UUID) throws UserNotExistException, FatalIOException {
        super(UUID);

        if (!isUser(UUID)){
            throw new UserNotExistException(UUID, "The user " + UUID + " doesn't exists");
        }

        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("SELECT PASSWORD, SALT FROM USERS WHERE (UUID = ?)");

            preparedStatement.setString(1, UUID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                passwordInfos = new Password.PasswordInfos(Base64.getDecoder().decode(resultSet.getString(1)), Base64.getDecoder().decode(resultSet.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new FatalIOException(ErrorCodes.couldnt_get_user_information, "Couldn't get user information for user " + UUID, e);
        }
    }

    public boolean equalsPassword(String clearPassword) throws InvalidKeySpecException {
        return Password.createPassword(clearPassword, passwordInfos.getSaltAsBytes()).getPasswordAsString().equals(passwordInfos.getPasswordAsString());
    }

    public static User createNewUser(String registerCode, String username, String password, String salt, String mailAddress) throws FatalIOException, UserAlreadyExistException, RegisterCodeNotExistException {
        if (isUser(username)) {
            throw new UserAlreadyExistException(username, "The user " + username + " already exists");
        }

        if (!RegisterCode.isRegisterCode(registerCode)) {
            throw new RegisterCodeNotExistException(registerCode);
        }

        String uuid = UUID.randomUUID().toString();

        File userDirectory = new File(new ServerFiles.user_files(uuid).user_files_dir);
        if (!userDirectory.mkdir()) {
            throw new FatalIOException(ErrorCodes.couldnt_create_userfiles_folder, "Couldn't create " + new ServerFiles.user_files(uuid).user_files_dir);
        }

        //--------------------------------------------------------------------------------------//

        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("INSERT INTO USERS (UUID, USERNAME, PASSWORD, SALT, EMAIL) VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, salt);
            preparedStatement.setString(5, mailAddress);

            preparedStatement.executeUpdate();

            SQL.checkpoint();
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_create_user_information, "Failed to add new user", e);
        }

        RegisterCode.removeRegisterCode(registerCode);

        try {
            return new User(username);
        } catch (UserNotExistException e) {
            //shouldn't get thrown
            return null;
        }

    }

    public static void deleteUser(String UUID) throws FatalIOException, IOException {
        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("DELETE FROM USERS WHERE UUID = ?");

            preparedStatement.setString(1, UUID);

            preparedStatement.executeUpdate();

            SQL.checkpoint();
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_delete_user_information, "Couldn't delete user information for user " + UUID, e);
        }

        File file;
        ArrayList<File> customUserFiles = FileUtils.getAllFilesInDirectory(new ServerFiles.user_files(UUID).user_files_dir);
        ListIterator<File> customUserFileListIterator = customUserFiles.listIterator(customUserFiles.size());

        while(customUserFileListIterator.hasPrevious()) {
            file = customUserFileListIterator.previous();

            if (!file.delete()) {
                if (file.isDirectory()) {
                    throw new FatalIOException(ErrorCodes.couldnt_delete_custom_user_directory, "Couldn't delete custom user folder " + file.getAbsolutePath());
                } else {
                    throw new FatalIOException(ErrorCodes.couldnt_delete_custom_user_file, "Couldn't delete custom user file " + file.getAbsolutePath());
                }
            }
        }

    }

    public static boolean isUser(String UUID) throws FatalIOException {
        try {
            Statement statement = Main.getConnection().createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT UUID FROM USERS");

            while (resultSet.next()) {
                if (resultSet.getString(1).equals(UUID)) {
                    return true;
                }
            }
            resultSet.close();

            return false;
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_get_user_information, "Couldn't get all usernames", e);
        }
    }

}
