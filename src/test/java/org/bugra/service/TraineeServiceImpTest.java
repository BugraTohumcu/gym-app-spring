package org.bugra.service;

import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.service.impl.TraineeServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImpTest {

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TrainingRepo trainingRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private UserCredentialsService userCredentialsService;

    @InjectMocks
    private TraineeServiceImp traineeService;


    private Trainee validTrainee(Long id) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        return trainee;
    }


    private RegisterTrainee createValidRegisterTrainee(){
        return new RegisterTrainee(
                "John",
                "Doe",
                LocalDate.of(2004, 7,7),
                "USA"
        );
    }

    @Test
    @DisplayName("Should successfully create trainee with generated credentials")
    void createTrainee_shouldSetCredentialsAndSave() {
        RegisterTrainee registerTrainee = createValidRegisterTrainee();

        when(userCredentialsService.generateRandomPassword()).thenReturn("Secret123");
        when(userCredentialsService.generateUsername("John", "Doe")).thenReturn("john.doe");

        when(traineeRepo.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee source = invocation.getArgument(0);
            source.setId(42L);
            return source;
        });

        UserResponse savedTrainee = traineeService.createTrainee(registerTrainee);

        assertNotNull(savedTrainee);

        assertEquals("Secret123", savedTrainee.password());
        assertEquals("john.doe", savedTrainee.username());

        verify(userCredentialsService, times(1)).generateRandomPassword();
        verify(userCredentialsService, times(1)).generateUsername("John", "Doe");
        verify(traineeRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Should successfully update trainee when valid")
    void updateTrainee_shouldReturnUpdatedTrainee() {
        Trainee trainee = validTrainee(1L);
        when(traineeRepo.update(trainee)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.updateTrainee(trainee);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(traineeRepo, times(1)).update(trainee);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existing trainee")
    void updateTrainee_shouldThrowWhenNotFound() {
        Trainee trainee = validTrainee(99L);

        when(traineeRepo.update(trainee)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> traineeService.updateTrainee(trainee));
        verify(traineeRepo, times(1)).update(trainee);
    }

    @Test
    @DisplayName("Should successfully delete trainee when trainee exists")
    void deleteTrainee_ById_shouldReturnTrue() {
        long id = 1L;
        when(traineeRepo.deleteById(id)).thenReturn(true);

        boolean result = traineeService.deleteTraineeById(id);

        assertTrue(result);
        verify(traineeRepo, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existing trainee")
    void deleteTrainee_ById_shouldThrowWhenNotFound() {
        long id = 99L;
        when(traineeRepo.deleteById(id)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> traineeService.deleteTraineeById(id));
        verify(traineeRepo, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should successfully return trainee when trainee exists")
    void getTrainee_shouldReturnTrainee() {
        long id = 1L;
        User mockUser = new User();
        mockUser.setUsername("john.doe");

        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(id);
        mockTrainee.setUser(mockUser);

        when(traineeRepo.findById(id)).thenReturn(Optional.of(mockTrainee));

        Trainee result = traineeService.getTrainee(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("john.doe", result.getUser().getUsername());
        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee does not exist")
    void getTrainee_shouldThrowWhenTraineeNotExists() {
        long id = 1L;
        when(traineeRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> traineeService.getTrainee(id));
        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return true when trainee successfully deleted by username")
    void deleteTraineeByUsername_shouldReturnTrueWhenSuccessful() {
        String username = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Set<Trainer> trainers = new HashSet<>();
        Trainer trainer = new Trainer();
        Set<Trainee> trainees = new HashSet<>();
        trainees.add(trainee);
        trainer.setTrainees(trainees);
        trainers.add(trainer);
        trainee.setTrainers(trainers);

        when(traineeRepo.findTraineeByUsername(username)).thenReturn(trainee);
        when(trainingRepo.deleteByTraineeId(1L)).thenReturn(true);
        when(traineeRepo.deleteById(1L)).thenReturn(true);

        boolean result = traineeService.deleteTraineeByUsername(username);

        assertTrue(result);
        assertTrue(trainers.isEmpty());
        assertTrue(trainees.isEmpty());
        verify(traineeRepo, times(1)).findTraineeByUsername(username);
        verify(trainingRepo, times(1)).deleteByTraineeId(1L);
        verify(traineeRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return true when trainee deleted even if no trainings existed")
    void deleteTraineeByUsername_shouldReturnTrueWhenNoTrainingsFound() {
        String username = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setTrainers(new HashSet<>());

        when(traineeRepo.findTraineeByUsername(username)).thenReturn(trainee);
        when(trainingRepo.deleteByTraineeId(1L)).thenReturn(false);
        when(traineeRepo.deleteById(1L)).thenReturn(true);

        boolean result = traineeService.deleteTraineeByUsername(username);

        assertTrue(result);
        verify(traineeRepo, times(1)).findTraineeByUsername(username);
        verify(trainingRepo, times(1)).deleteByTraineeId(1L);
        verify(traineeRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when repository delete fails")
    void deleteTraineeByUsername_shouldReturnFalseWhenDeleteFails() {
        String username = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setTrainers(null);

        when(traineeRepo.findTraineeByUsername(username)).thenReturn(trainee);
        when(trainingRepo.deleteByTraineeId(1L)).thenReturn(true);
        when(traineeRepo.deleteById(1L)).thenReturn(false);

        boolean result = traineeService.deleteTraineeByUsername(username);

        assertFalse(result);
        verify(traineeRepo, times(1)).findTraineeByUsername(username);
        verify(trainingRepo, times(1)).deleteByTraineeId(1L);
        verify(traineeRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee username not found")
    void deleteTraineeByUsername_shouldThrowWhenUsernameNotFound() {
        String username = "nonexistent";
        when(traineeRepo.findTraineeByUsername(username)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> traineeService.deleteTraineeByUsername(username));
        verify(traineeRepo, times(1)).findTraineeByUsername(username);
        verifyNoInteractions(trainingRepo);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee username does not exist")
    void updateTraineeTrainers_shouldThrowWhenTraineeNotFound() {
        // Arrange
        String traineeUsername = "nonexistent.trainee";
        List<String> trainerUsernames = List.of("jane.smith", "bob.brown");

        when(traineeRepo.findTraineeByUsername(traineeUsername)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames));

        verify(traineeRepo, times(1)).findTraineeByUsername(traineeUsername);
        verifyNoInteractions(trainerRepo);
        verify(traineeRepo, never()).update(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException with missing usernames when some trainers are not found")
    void updateTraineeTrainers_shouldThrowWhenSomeTrainersNotFound() {
        String traineeUsername = "john.doe";
        List<String> requestedTrainers = List.of("jane.smith", "ghost.trainer");

        Trainee mockTrainee = new Trainee();

        Trainer foundTrainer = new Trainer();
        User user = new User();
        user.setUsername("jane.smith");
        foundTrainer.setUser(user);
        List<Trainer> dbResult = List.of(foundTrainer);

        when(traineeRepo.findTraineeByUsername(traineeUsername)).thenReturn(mockTrainee);
        when(trainerRepo.findAllByUsernames(requestedTrainers)).thenReturn(dbResult);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTraineeTrainers(traineeUsername, requestedTrainers));

        assertTrue(exception.getMessage().contains("ghost.trainer"));

        verify(traineeRepo, times(1)).findTraineeByUsername(traineeUsername);
        verify(trainerRepo, times(1)).findAllByUsernames(requestedTrainers);
        verify(traineeRepo, never()).update(any());
    }

    @Test
    @DisplayName("Should successfully clear old trainers and set new ones when all data is valid")
    void updateTraineeTrainers_shouldSuccessfullyUpdateTrainersList() {
        String traineeUsername = "john.doe";
        List<String> newTrainersInput = List.of("jane.smith", "bob.brown");

        Trainee mockTrainee = new Trainee();
        Set<Trainer> currentTrainers = new java.util.HashSet<>();
        Trainer oldTrainer = new Trainer();
        currentTrainers.add(oldTrainer);
        mockTrainee.setTrainers(currentTrainers);

        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        List<Trainer> foundTrainersFromDb = List.of(trainer1, trainer2);

        when(traineeRepo.findTraineeByUsername(traineeUsername)).thenReturn(mockTrainee);
        when(trainerRepo.findAllByUsernames(newTrainersInput)).thenReturn(foundTrainersFromDb);
        when(traineeRepo.update(mockTrainee)).thenReturn(java.util.Optional.of(mockTrainee));

        assertDoesNotThrow(() -> traineeService.updateTraineeTrainers(traineeUsername, newTrainersInput));

        assertEquals(2, mockTrainee.getTrainers().size(), "Trainee's trainers count should be 2");
        assertTrue(mockTrainee.getTrainers().containsAll(foundTrainersFromDb), "Should contain all new trainers");
        assertFalse(mockTrainee.getTrainers().contains(oldTrainer), "Should be successfully removed");

        verify(traineeRepo, times(1)).findTraineeByUsername(traineeUsername);
        verify(trainerRepo, times(1)).findAllByUsernames(newTrainersInput);
        verify(traineeRepo, times(1)).update(mockTrainee);
    }
}