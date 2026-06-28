package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.persistence.repo.TraineeRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TraineeServiceImpTest {

    @Mock
    TraineeRepo traineeRepo;

    @InjectMocks
    TraineeServiceImp traineeService;

    @Test
    void createTrainee() {
    }

    @Test
    void updateTrainee() {
    }

    @Test
    void deleteTrainee() {
    }

    @Test
    @DisplayName("Should successfully return trainee when trainee exists")
    void getTrainee_shouldReturnTrainee() {

        //Given
        long id = 1L;
        String userName = "john.doe";
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(id);
        mockTrainee.setUsername(userName);

        // User exists return mock user
        when(traineeRepo.findById(id)).thenReturn(Optional.of(mockTrainee));

        Trainee result = traineeService.getTrainee(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(userName, result.getUsername());

        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee does not exists")
    void getTrainee_shouldThrowWhenTraineeNotExists(){
        long id = 1L;

        // user not exists return empty
        when(traineeRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.getTrainee(id));

        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should generate directly username if there is not similarity int storage")
    void generateUsername_shouldGenerateUsername() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe";

        String actualUsername = traineeService.generateUsername(firstName,lastName);

        assertEquals(expectedUserName, actualUsername);
    }

    @Test
    @DisplayName("Should append and increment counter when the generated username is already taken")
    void generateUsername_shouldHandleDuplicates() {
        String firstName = "John";
        String lastName = "Doe";
        String expectedUserName = "john.doe2";

        // The existing usernames
        when(traineeRepo.existsByUsername("john.doe")).thenReturn(true);
        when(traineeRepo.existsByUsername("john.doe1")).thenReturn(true);


        String actualUsername = traineeService.generateUsername(firstName,lastName);


        assertEquals(expectedUserName, actualUsername);

        verify(traineeRepo, times(1)).existsByUsername("john.doe");
        verify(traineeRepo, times(1)).existsByUsername("john.doe1");
        verify(traineeRepo, times(1)).existsByUsername(expectedUserName);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCredentials")
    @DisplayName("Should throw IllegalArgumentException when credentials are null or blank")
    void generateUsername_shouldThrowException_whenCredentialsAreInvalid(String firstName, String lastName) {

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.generateUsername(firstName, lastName));
    }

    private static Stream<Arguments> provideInvalidCredentials() {
        return Stream.of(
                Arguments.of(null, "Doe"),  // firstName null
                Arguments.of("John", null),  // lastName null
                Arguments.of("", "Doe"),    // firstName boş
                Arguments.of("John", " "),  // lastName blank
                Arguments.of(null, null)    // both null
        );
    }

    @Test
    void generateRandomPassword() {
    }
}