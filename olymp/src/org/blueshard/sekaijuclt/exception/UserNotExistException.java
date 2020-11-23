package org.blueshard.sekaijuclt.exception;

public class UserNotExistException extends UserException {

    public UserNotExistException(String username, String message){
        super(username, message);
    }

}
