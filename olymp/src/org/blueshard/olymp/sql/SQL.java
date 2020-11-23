package org.blueshard.olymp.sql;

import org.blueshard.olymp.Main;

import java.sql.*;

public class SQL {

    private Connection connection = null;

    public SQL () {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:file:/srv/hsqldb/olympdb/olympdb", "OLYMP", "&C=@zFR7kLQvGy%e");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public static void checkpoint() throws SQLException {
        Main.getConnection().createStatement().execute("CHECKPOINT");
    }

}
