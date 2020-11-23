package org.blueshard.olymp.register_code;

import org.blueshard.olymp.Main;
import org.blueshard.olymp.exception.ErrorCodes;
import org.blueshard.olymp.exception.FatalIOException;
import org.blueshard.olymp.sql.SQL;

import java.sql.*;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class RegisterCode {

    public static String generateNewRegisterCode() throws FatalIOException {
        String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 18; i++) {
            stringBuilder.append(letters[ThreadLocalRandom.current().nextInt(0, letters.length)]);
        }

        try {
            Connection connection = Main.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO REGISTER_CODES VALUES(?)");
            preparedStatement.setString(1, stringBuilder.toString());

            preparedStatement.executeUpdate();

            SQL.checkpoint();
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_create_register_code, "Couldn't add new register code", e);
        }

        return stringBuilder.toString();
    }

    public static HashSet<String> getAllRegisterCodes() throws FatalIOException {
        HashSet<String> registerCodes = new HashSet<>();

        try {
            Statement statement = Main.getConnection().createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT CODE FROM REGISTER_CODE");

            int columnCount;

            while (resultSet.next()) {
                registerCodes.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_get_register_code, "Failed to read register codes", e);
        }

        return registerCodes;
    }

    public static boolean isRegisterCode(String registerCode) throws FatalIOException {
        try {
            Statement statement = Main.getConnection().createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT CODE FROM REGISTER_CODE");

            while (resultSet.next()) {
                if (resultSet.getString(1).equals(registerCode)) {
                    resultSet.close();
                    return true;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_get_register_code, "Failed to read register codes", e);
        }

        return false;
    }

    public static void removeRegisterCode(String registerCode) throws FatalIOException {
        try {
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement("DELETE FROM REGISTER_CODES WHERE CODE = ?");

            preparedStatement.setString(1, registerCode);
            preparedStatement.executeUpdate();

            SQL.checkpoint();
        } catch (SQLException e) {
            throw new FatalIOException(ErrorCodes.couldnt_delete_register_code, "Failed to delete register code " + registerCode, e);
        }

    }

}
