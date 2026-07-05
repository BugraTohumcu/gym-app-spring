package org.bugra.service;

import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.impl.UserCredentialsServiceImp;
import org.bugra.util.PasswordGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCredentialsServiceImpTest {

    @Mock
    PasswordGenerator passwordGenerator;

    @Mock
    UserRepo userRepo;

    @InjectMocks
    UserCredentialsServiceImp credentialsService;

    @Test
    @DisplayName("Should generate directly username if there is not similarity in storage")
    void generateUsername_shouldGenerateUsername() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe";

        when(userRepo.findUsernameStartingWith("john.doe")).thenReturn(List.of());

        String actualUsername = credentialsService.generateUsername(firstName, lastName);

        assertEquals(expectedUserName, actualUsername);
    }

    @Test
    @DisplayName("Should append and increment counter when the generated username is already taken")
    void generateUsername_shouldHandleDuplicates() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe2";

        when(userRepo.findUsernameStartingWith("john.doe")).thenReturn(List.of("john.doe", "john.doe1"));

        String actualUsername = credentialsService.generateUsername(firstName, lastName);

        assertEquals(expectedUserName, actualUsername);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCredentials")
    @DisplayName("Should throw IllegalArgumentException when credentials are null or blank")
    void generateUsername_shouldThrowException_whenCredentialsAreInvalid(String firstName, String lastName) {
        assertThrows(IllegalArgumentException.class,
                () -> credentialsService.generateUsername(firstName, lastName));
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


    @Test
    @DisplayName("Should throw IllegalArgumentException when username is null")
    void toggleActiveStatus_shouldThrowWhenUsernameNull(){
        assertThrows(IllegalArgumentException.class,
                () -> credentialsService.toggleActiveStatus(null));
    }

    @Test
    @DisplayName("Should toggle active status from true to false")
    void toggleActiveStatus_shouldToggleFromTrueToFalse() {
        User user = new User();
        user.setActive(true);

        when(userRepo.findByUsername("test")).thenReturn(user);

        credentialsService.toggleActiveStatus("test");

        assertFalse(user.isActive());
        verify(userRepo).update(user);
    }

    @Test
    @DisplayName("Should toggle active status from false to true")
    void toggleActiveStatus_shouldToggleFromFalseToTrue() {
        User user = new User();
        user.setActive(false);

        when(userRepo.findByUsername("test")).thenReturn(user);

        credentialsService.toggleActiveStatus("test");

        assertTrue(user.isActive());
        verify(userRepo).update(user);
    }
}