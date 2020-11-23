package org.blueshard.olymp.exception;

public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException(String username, String message) {
        super(username, message);
    }

}
