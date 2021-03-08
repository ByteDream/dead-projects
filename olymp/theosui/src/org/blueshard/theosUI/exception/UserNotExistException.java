package org.blueshard.theosUI.exception;

public class UserNotExistException extends UserException {

    public UserNotExistException(String username, String message){
        super(username, message);
    }

}
