package org.bugra.service;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.service.impl.TrainerServiceImpl;
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
    TrainingTypeRepo trainingTypeRepo;

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
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setFirstName("Jane");
        trainer.getUser().setLastName("Smith");

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Running");
        trainer.setSpecialization(type);

        when(trainingTypeRepo.findByTrainingTypeName("Running")).thenReturn(Optional.empty());
        when(trainingTypeRepo.save(any(TrainingType.class))).thenReturn(type);

        when(userCredentialsService.generateRandomPassword()).thenReturn("Secret789");
        when(userCredentialsService.generateUsername("Jane", "Smith"))
                .thenReturn("jane.smith");
        when(trainerRepo.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer savedTrainer = trainerService.createTrainer(trainer);

        assertEquals("Secret789", savedTrainer.getUser().getPassword());
        assertEquals("jane.smith", savedTrainer.getUser().getUsername());
        assertTrue(savedTrainer.getUser().isActive());
        assertEquals(UserRole.TRAINER, savedTrainer.getUser().getRole());

        verify(trainerRepo, times(1)).save(trainer);
    }

    @Test
    @DisplayName("Should successfully update trainer when exists")
    void updateTrainer_shouldUpdateSuccessfully() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerRepo.update(trainer)).thenReturn(Optional.of(trainer));

        Trainer updated = trainerService.updateTrainer(trainer);

        assertNotNull(updated);
        verify(trainerRepo, times(1)).update(trainer);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException during update when trainer does not exist")
    void updateTrainer_shouldThrowExceptionWhenNotFound() {
        Trainer trainer = new Trainer();
        trainer.setId(99L);
        when(trainerRepo.update(trainer)).thenReturn(Optional.empty());

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