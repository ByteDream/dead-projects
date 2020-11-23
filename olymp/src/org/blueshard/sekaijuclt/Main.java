package org.blueshard.sekaijuclt;

import org.apache.log4j.Level;
import org.blueshard.sekaijuclt.client.MainClient;
import org.blueshard.sekaijuclt.logging.MainLogger;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MainClient client = new MainClient(8269, "192.168.2.104", new MainLogger(Level.ALL).getLogger());
        client.start("/srv/ssl/sslTrustStore.jks", "@VR&_*p%9!L+kC3FZ4QX");
    }

}
