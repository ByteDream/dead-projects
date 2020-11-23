package org.blueshard.olymp.exception;

public class FatalIOException extends Exception {

    private final int errno;
    private final Throwable throwable;

    public FatalIOException(int errno, String message) {
        super("Errno: " + errno + " - " + message);

        this.errno = errno;
        this.throwable = null;
    }

    public FatalIOException(int errno, String message, Throwable t) {
        super("Errno: " + errno + " - " + message);

        this.errno = errno;
        this.throwable = t;
    }

    public int getErrno() {
        return errno;
    }

    public Throwable getCauseThrowable() {
        return throwable;
    }

}
