package org.blueshard.olymp.e2ee;

import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.utils.ExceptionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSACipher {

    private static final int keySize = 2048;

    public static KeyPair generateKeyPair() throws UnexpectedException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedException(ExceptionUtils.extractExceptionMessage(e));
        }
    }

    public static byte[] encrypt(byte[] key, byte[] publicKey) throws UnexpectedException, InvalidKeyException, InvalidKeySpecException, BadPaddingException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            RSAPublicKey publicRSAKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicRSAKey);
            return cipher.doFinal(key);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new UnexpectedException(ExceptionUtils.extractExceptionMessage(e));
        }
    }

    public static byte[] decrypt(byte[] key, byte[] privateKey) throws UnexpectedException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privatePKCS8Key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privatePKCS8Key);
            return cipher.doFinal(key);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException e) {
            throw new UnexpectedException(ExceptionUtils.extractExceptionMessage(e));
        }
    }

}
