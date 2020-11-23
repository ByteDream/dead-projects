package org.blueshard.sekaijuclt.exception;

public class FatalIOException extends Exception {

    private final int errno;

    public FatalIOException(int errno, String message) {
        super("Errno: " + errno + " - " + message);

        this.errno = errno;
    }

    public int getErrno() {
        return errno;
    }

}
