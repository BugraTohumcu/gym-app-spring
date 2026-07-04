package org.bugra.service;

import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.persistence.repo.TrainingRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImpTest {

    @Mock
    TrainingRepo trainingRepo;

    @Mock
    TrainerService trainerService;

    @Mock
    TraineeService traineeService;

    @InjectMocks
    TrainingServiceImp trainingService;

    @Test
    @DisplayName("Should successfully create training when data is valid")
    void createTraining_shouldSaveSuccessfully() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        training.setTrainingDuration(60);

        when(traineeService.existsById(1L)).thenReturn(true);
        when(trainerService.existsById(1L)).thenReturn(true);

        Training savedTraining = new Training();
        savedTraining.setId(1L);
        when(trainingRepo.save(any(Training.class))).thenReturn(savedTraining);

        Training saved = trainingService.createTraining(training);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(trainingRepo, times(1)).save(any(Training.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when date is in the past")
    void createTraining_shouldThrowExceptionForPastDate() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now().minusDays(1));
        training.setTrainingDuration(60);

        when(traineeService.existsById(1L)).thenReturn(true);
        when(trainerService.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }

    @Test
    @DisplayName("Should throw exception when duration is non-positive")
    void createTraining_shouldThrowExceptionForInvalidDuration() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        training.setTrainingDuration(0);

        when(traineeService.existsById(1L)).thenReturn(true);
        when(trainerService.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee id does not exists")
    void createTraining_shouldThrowWhenTraineeIdNotExist(){
        Trainee trainee = new Trainee();
        trainee.setId(999L);

        Training training = new Training();
        training.setTrainee(trainee);
        when(traineeService.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> trainingService.createTraining(training));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainer id does not exists")
    void createTraining_shouldThrowWhenTrainerNotExist() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(999L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        when(traineeService.existsById(1L)).thenReturn(true);
        when(trainerService.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> trainingService.createTraining(training));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully get training when exists")
    void getTraining_shouldReturnTraining() {
        long id = 1L;
        String trainingName = "Swimming Training";
        Training mockTraining = new Training();
        mockTraining.setTrainingName(trainingName);
        when(trainingRepo.findById(id)).thenReturn(Optional.of(mockTraining));

        Training result = trainingService.getTraining(id);

        assertEquals(trainingName, result.getTrainingName());
        verify(trainingRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw TrainingNotFoundException when training not found")
    void getTraining_shouldThrowExceptionWhenNotFound() {
        long id = 1L;
        when(trainingRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class,
                () -> trainingService.getTraining(id));
    }

    @Test
    @DisplayName("Should validate for correct inputs")
    void validateTrainingDateAndDuration_shouldPassForValidInput() {
        assertDoesNotThrow(() ->
                trainingService.validateTrainingDateAndDuration(LocalDate.now().plusDays(5), 30));
    }

    @Test
    @DisplayName("Should throw exception for past date")
    void validate_shouldThrowExceptionForPastDate() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.validateTrainingDateAndDuration(LocalDate.now().minusDays(1), 60));
    }
}