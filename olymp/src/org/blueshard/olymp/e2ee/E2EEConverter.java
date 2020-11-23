package org.blueshard.olymp.e2ee;

import org.blueshard.olymp.exception.UnexpectedException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class E2EEConverter {

    private final static String separator = "@";

    public static String decrypt(String input, byte[] privateKey) throws UnexpectedException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException {
        String[] inputAsArray = input.split(separator);
        String key = inputAsArray[0];
        String realInput = inputAsArray[1];

        byte[] AESKey = RSACipher.decrypt(Base64.getDecoder().decode(key), privateKey);

        return new String(AESCipher.decrypt(Base64.getDecoder().decode(realInput), AESKey));
    }

    public static String encrypt(String input, byte[] publicKey) throws UnexpectedException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] AESKey = AESCipher.generateKey();

        String encryptedKey = Base64.getEncoder().encodeToString(RSACipher.encrypt(AESKey, publicKey));
        String encryptedInput = Base64.getEncoder().encodeToString(AESCipher.encrypt(input.getBytes(StandardCharsets.UTF_8), AESKey));

        return encryptedKey + separator + encryptedInput;
    }

}
