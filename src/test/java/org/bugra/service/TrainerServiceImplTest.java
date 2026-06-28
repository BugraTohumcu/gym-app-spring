package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.persistence.repo.TrainerRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    TrainerRepo trainerRepo;

    @Mock
    UserCredentialsService userCredentialsService;

    @InjectMocks
    TrainerServiceImpl trainerService;

    @Test
    @DisplayName("Should throw IllegalArgumentException when trainer is null")
    void createTrainer_shouldThrowExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(null));
    }

    @Test
    @DisplayName("Should successfully create trainer with generated credentials")
    void createTrainer_shouldSetCredentialsAndSave() {
        // Given
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        when(userCredentialsService.generateRandomPassword()).thenReturn("Secret789");
        when(userCredentialsService.generateUsername(eq("Jane"), eq("Smith"), any()))
                .thenReturn("jane.smith");
        when(trainerRepo.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Trainer savedTrainer = trainerService.createTrainer(trainer);

        // Then
        assertEquals("Secret789", savedTrainer.getPassword());
        assertEquals("jane.smith", savedTrainer.getUsername());
        verify(trainerRepo, times(1)).save(trainer);
    }

    @Test
    @DisplayName("Should successfully update trainer when exists")
    void updateTrainer_shouldUpdateSuccessfully() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerRepo.updateById(trainer)).thenReturn(Optional.of(trainer));

        Trainer updated = trainerService.updateTrainer(trainer);

        assertNotNull(updated);
        verify(trainerRepo, times(1)).updateById(trainer);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException during update when trainer does not exist")
    void updateTrainer_shouldThrowExceptionWhenNotFound() {
        Trainer trainer = new Trainer();
        trainer.setId(99L);
        when(trainerRepo.updateById(trainer)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> trainerService.updateTrainer(trainer));
    }

    @Test
    @DisplayName("Should successfully get trainer by id")
    void getTrainer_shouldReturnTrainer() {
        long id = 1L;
        Trainer mockTrainer = new Trainer();
        mockTrainer.setId(id);

        when(trainerRepo.findById(id)).thenReturn(Optional.of(mockTrainer));

        Trainer result = trainerService.getTrainer(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(trainerRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainer does not exist")
    void getTrainer_shouldThrowWhenTrainerNotExists() {
        long id = 1L;
        when(trainerRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> trainerService.getTrainer(id));
        verify(trainerRepo, times(1)).findById(id);
    }
}