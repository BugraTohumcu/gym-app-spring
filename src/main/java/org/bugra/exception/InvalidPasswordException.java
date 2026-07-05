package org.bugra.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
    public InvalidPasswordException() {
        super("Invalid password provided");
    }
}
