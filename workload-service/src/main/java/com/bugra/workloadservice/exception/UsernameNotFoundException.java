package com.bugra.workloadservice.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
    public UsernameNotFoundException() {
        super("There is no user with provided username");
    }
}
