package org.bugra.exception;

public class InvalidPassowordException extends RuntimeException {
    public InvalidPassowordException(String message) {
        super(message);
    }
    public InvalidPassowordException() {
        super("Invalid password provided");
    }
}
