package org.blueshard.sekaijuclt.exception;

public class UserException extends Exception {

    final String username;

    public UserException(String username, String message) {
        super(message);

        this.username = username;
    }

}
