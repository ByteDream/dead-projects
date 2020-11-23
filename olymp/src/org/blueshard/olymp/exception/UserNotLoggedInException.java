package org.blueshard.olymp.exception;

public class UserNotLoggedInException extends UserException {

    public UserNotLoggedInException(String username, String message) {
        super(username, message);
    }
}
