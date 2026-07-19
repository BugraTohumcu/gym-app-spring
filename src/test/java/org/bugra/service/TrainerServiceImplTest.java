package org.bugra.service;

import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.request.UpdateTrainer;
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


    private Trainer validTrainer(Long id) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Swimming");

        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }

    private UpdateTrainer createValidUpdateTrainer(){
        return new UpdateTrainer(
                "john.doe",
                "John",
                "Doe",
                "Swimming",
                true
        );
    }

    @Test
    @DisplayName("Should successfully create trainer with generated credentials")
    void createTrainer_shouldSetCredentialsAndSave() {

        RegisterTrainer registerTrainer = new RegisterTrainer(
                "Jane",
                "Smith",
                "Swimming"
        );


        TrainingType trainingType = new TrainingType();

        when(trainingTypeRepo.findByTrainingTypeName(anyString())).thenReturn(Optional.of(trainingType));

        when(userCredentialsService.generateRandomPassword()).thenReturn("Secret789");
        when(userCredentialsService.generateUsername("Jane", "Smith"))
                .thenReturn("jane.smith");
        when(trainerRepo.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer savedTrainer = trainerService.createTrainer(registerTrainer);

        assertEquals("Secret789", savedTrainer.getUser().getPassword());
        assertEquals("jane.smith", savedTrainer.getUser().getUsername());
        assertTrue(savedTrainer.getUser().isActive());
        assertEquals(UserRole.TRAINER, savedTrainer.getUser().getRole());

        verify(trainerRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Should successfully update trainer when exists")
    void updateTrainer_shouldUpdateSuccessfully() {

        UpdateTrainer updateTrainer = createValidUpdateTrainer();

        Trainer trainer = validTrainer(1L);
        when(trainerRepo.findTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainerRepo.update(trainer)).thenReturn(Optional.of(trainer));

        Trainer updated = trainerService.updateTrainer(updateTrainer);

        assertNotNull(updated);
        verify(trainerRepo, times(1)).update(trainer);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException during update when trainer does not exist")
    void updateTrainer_shouldThrowExceptionWhenNotFound() {

        when(trainerRepo.findTrainerByUsername(anyString())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> trainerService.updateTrainer(createValidUpdateTrainer()));
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