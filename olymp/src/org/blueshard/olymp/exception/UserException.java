package org.blueshard.olymp.exception;

public class UserException extends Exception {

    final String UUID;

    public UserException(String UUID, String message) {
        super(message);

        this.UUID = UUID;
    }

}
