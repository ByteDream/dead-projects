package org.blueshard.olymp;

import org.apache.log4j.Logger;
import org.blueshard.olymp.exception.FatalIOException;
import org.blueshard.olymp.logging.EmptyLogger;
import org.blueshard.olymp.logging.ServerLogger;
import org.blueshard.olymp.register_code.RegisterCode;
import org.blueshard.olymp.server.MainServer;
import org.blueshard.olymp.sql.SQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static Connection connection = new SQL().getConnection();
    public Logger root = new EmptyLogger().getLogger();

    public static void main(String[] args) throws IOException, SQLException {
        if (args.length == 1) {
            String command = args[0].strip().toLowerCase();
            if (command.equals("sql")) {
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                Connection connection = getConnection();
                String SQLQuery;
                while (true) {
                    System.out.print("SQL Query: ");
                    SQLQuery = input.readLine().strip().toUpperCase();

                    if (SQLQuery.equals("EXIT")) {
                        break;
                    } else {
                        connection.createStatement().executeQuery(SQLQuery);
                    }
                }
            } else if (command.equals("add registercode")) {
                try {
                    RegisterCode.generateNewRegisterCode();
                } catch (FatalIOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MainServer server = new MainServer(8269, new ServerLogger().main());
            server.start();

            connection.close();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

}
