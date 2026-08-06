package org.bugra.exception;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String message) {
        super(message);
    }

    public TrainingTypeNotFoundException() {
        super("Provided Training type does not exists");
    }
}
