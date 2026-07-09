package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.service.impl.TraineeServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
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
    private UserCredentialsService userCredentialsService;

    @InjectMocks
    private TraineeServiceImp traineeService;

    @Test
    @DisplayName("Should throw exception when trainee is null")
    void createTrainee_shouldThrowExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
    }

    @Test
    @DisplayName("Should throw exception when trainee user is null")
    void createTrainee_shouldThrowExceptionWhenUserIsNull() {
        Trainee trainee = new Trainee();
        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(trainee));
    }

    @Test
    @DisplayName("Should successfully create trainee with generated credentials")
    void createTrainee_shouldSetCredentialsAndSave() {
        User user = new User();
        user.setFirstName("john");
        user.setLastName("doe");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(userCredentialsService.generateRandomPassword()).thenReturn("Secret123");
        when(userCredentialsService.generateUsername("john", "doe")).thenReturn("john.doe");

        when(traineeRepo.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee source = invocation.getArgument(0);
            source.setId(42L);
            return source;
        });

        Trainee savedTrainee = traineeService.createTrainee(trainee);

        assertNotNull(savedTrainee);
        assertEquals(42L, savedTrainee.getId());

        User savedUser = savedTrainee.getUser();
        assertNotNull(savedUser);
        assertEquals("Secret123", savedUser.getPassword());
        assertEquals("john.doe", savedUser.getUsername());
        assertTrue(savedUser.isActive());

        verify(userCredentialsService, times(1)).generateRandomPassword();
        verify(userCredentialsService, times(1)).generateUsername("john", "doe");
        verify(traineeRepo, times(1)).save(trainee);
    }

    @Test
    @DisplayName("Should successfully update trainee when valid")
    void updateTrainee_shouldReturnUpdatedTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeRepo.update(trainee)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.updateTrainee(trainee);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(traineeRepo, times(1)).update(trainee);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existing trainee")
    void updateTrainee_shouldThrowWhenNotFound() {
        Trainee trainee = new Trainee();
        trainee.setId(99L);

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
}