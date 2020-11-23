package org.blueshard.olymp.cli;

import org.blueshard.olymp.exception.FatalIOException;
import org.blueshard.olymp.register_code.RegisterCode;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, FatalIOException, SQLException {
        switch (args[0].strip()) {
            case "start":
                org.blueshard.olymp.Main.main(new String[0]);
            case "add register code":
                RegisterCode.generateNewRegisterCode();
        }
    }

}
