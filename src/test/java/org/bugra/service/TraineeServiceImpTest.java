package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.persistence.repo.TraineeRepo;
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

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TraineeServiceImpTest {

    @Mock
    TraineeRepo traineeRepo;

    @Mock
    UserCredentialsServiceImp userCredentialsServiceImp;

    @InjectMocks
    TraineeServiceImp traineeService;

    @Test
    @DisplayName("Should throw exception when trainee is null")
    void createTrainee_shouldThrowExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
    }
    @Test
    @DisplayName("Should successfully create trainee with generated credentials")
    void createTrainee_shouldSetCredentialsAndSave() {
        // Given
        Trainee trainee = new Trainee();
        trainee.setFirstName("john");
        trainee.setLastName("doe");

        when(userCredentialsServiceImp.generateRandomPassword()).thenReturn("Secret123");
        when(userCredentialsServiceImp.generateUsername(eq("john"), eq("doe"), any()))
                .thenReturn("john.doe");
        when(traineeRepo.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Trainee savedTrainee = traineeService.createTrainee(trainee);

        // Then
        assertEquals("Secret123", savedTrainee.getPassword());
        assertEquals("john.doe", savedTrainee.getUsername());
        verify(traineeRepo, times(1)).save(trainee);
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

    private static Stream<Arguments> provideInvalidCredentials() {
        return Stream.of(
                Arguments.of(null, "Doe"),  // firstName null
                Arguments.of("John", null),  // lastName null
                Arguments.of("", "Doe"),    // firstName boş
                Arguments.of("John", " "),  // lastName blank
                Arguments.of(null, null)    // both null
        );
    }

}