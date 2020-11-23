package org.blueshard.olymp.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Password {

    public static PasswordInfos createPassword(String password, byte[] salt) throws InvalidKeySpecException {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException ignore) {
        }

        byte[] hashedPassword;
        try {
            hashedPassword = factory.generateSecret(keySpec).getEncoded();
        } catch (InvalidKeySpecException ignore) {
        }

        hashedPassword = factory.generateSecret(keySpec).getEncoded();

        return new PasswordInfos(hashedPassword, salt);
    }

    public static class PasswordInfos {

        private final byte[] password;
        private final byte[] salt;

        public PasswordInfos(byte[] password, byte[] salt) {
            this.password = password;
            this.salt = salt;
        }

        public byte[] getPasswordAsBytes() {
            return password;
        }

        public String getPasswordAsString() {
            Base64.Encoder encoder = Base64.getEncoder();

            return encoder.encodeToString(password);
        }

        public byte[] getSaltAsBytes() {
            return salt;
        }

        public String getSaltAsString() {
            Base64.Encoder encoder = Base64.getEncoder();

            return encoder.encodeToString(salt);
        }

    }

}
