package org.blueshard.olymp.exception;

public class UnexpectedException extends Exception {

    public UnexpectedException() {
        super("An unexpected error occurred");
    }

    public UnexpectedException(Throwable t) {
        super("An unexpected error occurred (" + t.getMessage() + ")");
        this.setStackTrace(t.getStackTrace());
    }

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable t) {
        super(message);
        this.setStackTrace(t.getStackTrace());
    }

}
