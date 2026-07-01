package org.bugra.service;

import org.bugra.util.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserCredentialsServiceImpTest {

    @Mock
    PasswordGenerator passwordGenerator;

    @InjectMocks
    UserCredentialsServiceImp credentialsService;


    @Test
    @DisplayName("Should generate directly username if there is not similarity int storage")
    void generateUsername_shouldGenerateUsername() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe";

        String actualUsername = credentialsService.generateUsername(firstName,
                lastName,
                s -> false);

        assertEquals(expectedUserName, actualUsername);
    }

    @Test
    @DisplayName("Should append and increment counter when the generated username is already taken")
    void generateUsername_shouldHandleDuplicates() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe2";


        // The existing usernames
        var takenUsernames = Set.of("john.doe", "john.doe1");

        String actualUsername = credentialsService.generateUsername(
                firstName,
                lastName,
                takenUsernames::contains
        );

        assertEquals(expectedUserName, actualUsername);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCredentials")
    @DisplayName("Should throw IllegalArgumentException when credentials are null or blank")
    void generateUsername_shouldThrowException_whenCredentialsAreInvalid(String firstName, String lastName) {


        assertThrows(IllegalArgumentException.class,
                () -> credentialsService.generateUsername(firstName, lastName, (s) -> false));
    }

    private static Stream<Arguments> provideInvalidCredentials() {
        return Stream.of(
                Arguments.of(null, "Doe"),
                Arguments.of("John", null),
                Arguments.of("", "Doe"),
                Arguments.of("John", " "),
                Arguments.of(null, null)
        );
    }


    @Test
    @DisplayName("Should generate random password using the password generator")
    void generateRandomPassword_shouldReturnGeneratedPassword() {

        String expectedPassword = "RandomPassword123";
        when(passwordGenerator.generate()).thenReturn(expectedPassword);

        String actualPassword = credentialsService.generateRandomPassword();

        assertEquals(expectedPassword, actualPassword);
        verify(passwordGenerator, times(1)).generate();
    }
}