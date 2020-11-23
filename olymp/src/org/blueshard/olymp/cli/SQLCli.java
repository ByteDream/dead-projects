package org.blueshard.olymp.cli;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;

public class SQLCli {

    public static void main(String[] args) throws IOException {
        Socket server = SocketFactory.getDefault().createSocket("localhost", 8269);

        
    }

}
