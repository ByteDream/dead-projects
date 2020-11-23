package org.blueshard.olymp.user;

import org.blueshard.olymp.Main;
import org.blueshard.olymp.exception.ErrorCodes;
import org.blueshard.olymp.exception.FatalIOException;
import org.blueshard.olymp.files.ConfReader;
import org.blueshard.olymp.files.ConfWriter;
import org.blueshard.olymp.files.ServerFiles;
import org.blueshard.olymp.security.Password;
import org.blueshard.olymp.sql.SQL;
import org.blueshard.olymp.sql.SQLPosition;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.TreeMap;

public class UserInfos {

    private final String UUID;
    private String username;
    private Password.PasswordInfos passwordInfos;
    private String mail;
    private boolean active;
    private int userLevel;
    private int logLevel;
    private long maxFiles;
    private long files;
    private double maxFilesSize;
    private double filesSize;

    public UserInfos(String UUID) throws FatalIOException {
        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("SELECT * FROM USERS WHERE (UUID = ?)");

            preparedStatement.setString(1, UUID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                this.UUID = UUID;
                this.username = resultSet.getString(SQLPosition.USERS.USERNAME);
                this.passwordInfos = new Password.PasswordInfos(Base64.getDecoder().decode(resultSet.getString(SQLPosition.USERS.PASSWORD)),
                        Base64.getDecoder().decode(resultSet.getString(SQLPosition.USERS.SALT)));
                this.mail = resultSet.getString(SQLPosition.USERS.MAIL);
                this.active = resultSet.getBoolean(SQLPosition.USERS.ACTIVE);
                this.userLevel = resultSet.getInt(SQLPosition.USERS.USERLEVEL);
                this.logLevel = resultSet.getInt(SQLPosition.USERS.LOGLEVEL);
                this.maxFiles = resultSet.getLong(SQLPosition.USERS.MAX_FILES);
                this.files = resultSet.getLong(SQLPosition.USERS.FILES);
                this.maxFilesSize = resultSet.getDouble(SQLPosition.USERS.MAX_FILES_SIZE);
                this.filesSize = resultSet.getDouble(SQLPosition.USERS.FILES_SIZE);
            } else {
                throw new FatalIOException(ErrorCodes.couldnt_get_user_information, "Couldn't get user information for user " + UUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new FatalIOException(ErrorCodes.couldnt_get_user_information, "Couldn't get user information for user " + UUID, e);
        }
    }

    public String getUUID() {
        return UUID;
    }

    public String getUsername() {
        return username;
    }

    public Password.PasswordInfos getPasswordInfos() {
        return passwordInfos;
    }

    public String getMail() {
        return mail;
    }

    public boolean getActive() {
        return active;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public long getMaxFiles() {
        return maxFiles;
    }

    public long getFiles() {
        return files;
    }

    public double getMaxFilesSize() {
        return maxFilesSize;
    }

    public double getFilesSize() {
        return filesSize;
    }

    public String getUserfileDirectory() {
        return new ServerFiles.user_files(getUUID()).user_files_dir;
    }

}
