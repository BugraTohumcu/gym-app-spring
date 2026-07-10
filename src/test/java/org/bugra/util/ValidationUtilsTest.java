package org.bugra.util;

import org.bugra.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    @DisplayName("Should throw ValidationException when provided value is null")
    void requireNotNull_shouldThrowWhenValueNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireNotNull(null, "test"));
    }

    @Test
    @DisplayName("Should not throw when provided value is not null")
    void requireNotNull_shouldNotThrowWhenValuePresent() {
        assertDoesNotThrow(() -> ValidationUtils.requireNotNull("value", "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when provided field is blank")
    void requireNonBlank_shouldThrowWhenValueBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireNonBlank("", "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when provided field is null")
    void requireNonBlank_shouldThrowWhenValueNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireNonBlank(null, "test"));
    }

    @Test
    @DisplayName("Should not throw when provided field has content")
    void requireNonBlank_shouldNotThrowWhenValuePresent() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonBlank("John", "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when value is shorter than min length")
    void requireLengthBetween_shouldThrowWhenTooShort() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireLengthBetween("a", 2, 50, "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when value is longer than max length")
    void requireLengthBetween_shouldThrowWhenTooLong() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireLengthBetween("a".repeat(51), 2, 50, "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when value is blank")
    void requireLengthBetween_shouldThrowWhenBlank() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireLengthBetween("", 2, 50, "test"));
    }

    @Test
    @DisplayName("Should not throw when value length is within range")
    void requireLengthBetween_shouldNotThrowWhenWithinRange() {
        assertDoesNotThrow(() -> ValidationUtils.requireLengthBetween("John", 2, 50, "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when date is null")
    void requirePastDate_shouldThrowWhenDateNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requirePastDate(null, "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when date is today")
    void requirePastDate_shouldThrowWhenDateIsToday() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requirePastDate(LocalDate.now(), "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when date is in the future")
    void requirePastDate_shouldThrowWhenDateInFuture() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requirePastDate(LocalDate.now().plusDays(1), "test"));
    }

    @Test
    @DisplayName("Should not throw when date is in the past")
    void requirePastDate_shouldNotThrowWhenDateInPast() {
        assertDoesNotThrow(() -> ValidationUtils.requirePastDate(LocalDate.now().minusYears(20), "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when collection is null")
    void requireNotEmpty_shouldThrowWhenCollectionNull() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireNotEmpty(null, "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when collection is empty")
    void requireNotEmpty_shouldThrowWhenCollectionEmpty() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireNotEmpty(List.of(), "test"));
    }

    @Test
    @DisplayName("Should not throw when collection has elements")
    void requireNotEmpty_shouldNotThrowWhenCollectionHasElements() {
        assertDoesNotThrow(() -> ValidationUtils.requireNotEmpty(List.of("a"), "test"));
    }

    @Test
    @DisplayName("Should throw ValidationException when condition is false")
    void requireTrue_shouldThrowWhenConditionFalse() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireTrue(false, "condition failed"));
    }

    @Test
    @DisplayName("Should not throw when condition is true")
    void requireTrue_shouldNotThrowWhenConditionTrue() {
        assertDoesNotThrow(() -> ValidationUtils.requireTrue(true, "condition failed"));
    }

    @Test
    @DisplayName("Should throw ValidationException when value exceeds max length")
    void requireMaxLength_shouldThrowWhenTooLong() {
        assertThrows(ValidationException.class,
                () -> ValidationUtils.requireMaxLength("a".repeat(11), 10, "test"));
    }

    @Test
    @DisplayName("Should not throw when value is null")
    void requireMaxLength_shouldNotThrowWhenValueNull() {
        assertDoesNotThrow(() -> ValidationUtils.requireMaxLength(null, 10, "test"));
    }

    @Test
    @DisplayName("Should not throw when value is within max length")
    void requireMaxLength_shouldNotThrowWhenWithinLimit() {
        assertDoesNotThrow(() -> ValidationUtils.requireMaxLength("John", 10, "test"));
    }
}