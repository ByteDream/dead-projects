package org.blueshard.olymp.exception;

public class IllegalCodeException extends Exception {

    private final int givenCode;
    private final int requiredCode;

    public IllegalCodeException(int givenCode, int requiredCode){
        super("Wrong data code is given '" + givenCode + "', expected '" + requiredCode + "'");

        this.givenCode = givenCode;
        this.requiredCode = requiredCode;
    }

}
