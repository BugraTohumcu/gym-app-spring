package org.bugra.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    @DisplayName("Should generate password with exactly 10 characters")
    void generate_shouldReturnCorrectLength() {
        // When
        String password = passwordGenerator.generate();

        // Then
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    @DisplayName("Should not return blank string")
    void generate_shouldNotBeBlank() {
        // When
        String password = passwordGenerator.generate();

        // Then
        assertFalse(password.isBlank());
    }

    @Test
    @DisplayName("Should generate unique passwords on consecutive calls")
    void generate_shouldBeUniqueEachTime() {
        String firstPassword = passwordGenerator.generate();
        String secondPassword = passwordGenerator.generate();

        assertNotEquals(firstPassword, secondPassword);
    }
}