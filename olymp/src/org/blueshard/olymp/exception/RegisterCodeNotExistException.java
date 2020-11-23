package org.blueshard.olymp.exception;

public class RegisterCodeNotExistException extends Exception {

    public RegisterCodeNotExistException(String registerCode) {
        super("The register code '" + registerCode + "' do not exist");
    }

}
