package org.blueshard.olymp.fileserver;

import org.apache.log4j.Logger;
import org.blueshard.olymp.e2ee.E2EEConverter;
import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.server.ClientFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class FileReceiver {

    ClientFactory.Client client;
    File destFile;
    byte[] privateKey;
    Logger logger;

    public FileReceiver(ClientFactory.Client client, File destFile, byte[] privateKey, Logger logger) {
        this.client = client;
        this.destFile = destFile;
        this.privateKey = privateKey;
        this.logger = logger;
    }

    public String main() throws IOException, UnexpectedException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, NoSuchPaddingException {
        try {
            SSLServerSocket FTPSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(20);
            logger.info("Start File Receiver for client " + client.getId());
            logger.info("Receiver infos: " + FTPSocket.toString());

            SSLSocket sslSocket = (SSLSocket) FTPSocket.accept();
            logger.info("Client connected");

            InputStream inputStream = sslSocket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(destFile);
            StringBuilder hexString = new StringBuilder();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(Base64.getDecoder().decode(E2EEConverter.decrypt(Base64.getEncoder().encodeToString(buffer), privateKey)), 0, length);
                md5.update(buffer, 0, length);
            }
            logger.info("Received file from client and saved it");

            byte[] digest = md5.digest();

            for (byte b : digest) {
                if ((0xff & b) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & b)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & b));
                }
            }
            sslSocket.close();
            logger.info("Closed File Receiver");
            logger.info("File MD5 check sum is " + hexString.toString());

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedException(e);
        }
    }

}
