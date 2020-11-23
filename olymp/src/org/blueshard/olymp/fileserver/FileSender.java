package org.blueshard.olymp.fileserver;

import org.apache.log4j.Logger;
import org.blueshard.olymp.e2ee.E2EEConverter;
import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.server.ClientFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class FileSender {

    ClientFactory.Client client;
    File sourceFile;
    byte[] publicKey;
    Logger logger;

    public FileSender(ClientFactory.Client client, File sourceFile, byte[] publicKey, Logger logger) {
        this.client = client;
        this.sourceFile = sourceFile;
        this.publicKey = publicKey;
        this.logger = logger;
    }

    public String main() throws IOException, UnexpectedException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidKeyException {
        try {
            SSLServerSocket FTPSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(20);
            logger.info("Started file sender");
            logger.info("Sender infos: " + FTPSocket.toString());

            SSLSocket sslSocket = (SSLSocket) FTPSocket.accept();
            logger.info("Client connected");

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            OutputStream outputStream = sslSocket.getOutputStream();
            StringBuilder hexString = new StringBuilder();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length;

            while ((length = fileInputStream.read(buffer)) > 0) {
                outputStream.write(Base64.getDecoder().decode(E2EEConverter.encrypt(Base64.getEncoder().encodeToString(buffer), publicKey)), 0, length);
                md5.update(buffer, 0, length);
            }
            logger.info("Send file to client and saved it");

            byte[] digest = md5.digest();

            for (byte b : digest) {
                if ((0xff & b) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & b)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & b));
                }
            }
            sslSocket.close();
            logger.info("Closed File Sender");

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedException(e);
        }
    }

}
