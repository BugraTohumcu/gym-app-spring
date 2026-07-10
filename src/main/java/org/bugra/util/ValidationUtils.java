package org.bugra.util;


import org.bugra.exception.ValidationException;
import java.time.LocalDate;
import java.util.Collection;


/**
 * <p>This class provides validation utils function for entity specific validations</p>
 * */
public final class ValidationUtils {

    private ValidationUtils() { }


    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    public static void requireLengthBetween(String value, int min, int max, String fieldName) {
        requireNonBlank(value, fieldName);
        int len = value.trim().length();
        if (len < min || len > max) {
            throw new ValidationException(
                    fieldName + " must be between " + min + " and " + max + " characters");
        }
    }

    public static void requirePastDate(LocalDate date, String fieldName) {
        requireNotNull(date, fieldName);
        if (!date.isBefore(LocalDate.now())) {
            throw new ValidationException(fieldName + " must be a date in the past");
        }
    }

    public static void requireNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    public static void requireMaxLength(String value, int max, String fieldName) {
        if (value != null && value.trim().length() > max) {
            throw new ValidationException(fieldName + " must be at most " + max + " characters");
        }
    }
}