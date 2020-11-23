package org.blueshard.olymp.e2ee;

import org.blueshard.olymp.exception.UnexpectedException;
import org.blueshard.olymp.utils.ExceptionUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESCipher {

    private static final int keySize = 256;

    public static byte[] generateKey() throws UnexpectedException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedException(ExceptionUtils.extractExceptionMessage(e));
        }
    }

    public static byte[] encrypt(byte[] input, byte[] key) throws UnexpectedException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        try {
            SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new UnexpectedException(e);
        }
    }

    public static byte[] decrypt(byte[] input, byte[] key) throws UnexpectedException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        try {
            SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(input);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new UnexpectedException(e);
        }
    }

}
