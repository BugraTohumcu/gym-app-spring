package org.bugra.service;

import org.bugra.exception.TrainingNotFoundException;
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

    @InjectMocks
    TrainingServiceImp trainingService;

    @Test
    @DisplayName("Should successfully create training when data is valid")
    void createTraining_shouldSaveSuccessfully() {
        Training training = new Training();

        // Future data
        training.setTrainingDate(LocalDate.now().plusDays(1));
        training.setTrainingDuration(60);

        when(trainingRepo.save(any(Training.class))).thenAnswer(i -> i.getArguments()[0]);

        Training saved = trainingService.createTraining(training);

        assertNotNull(saved);
        verify(trainingRepo, times(1)).save(training);
    }

    @Test
    @DisplayName("Should throw exception when date is in the past")
    void createTraining_shouldThrowExceptionForPastDate() {
        Training training = new Training();
        training.setTrainingDate(LocalDate.now().minusDays(1)); // Geçmiş tarih
        training.setTrainingDuration(60);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
    }

    @Test
    @DisplayName("Should throw exception when duration is non-positive")
    void createTraining_shouldThrowExceptionForInvalidDuration() {
        Training training = new Training();
        training.setTrainingDate(LocalDate.now().plusDays(1));

        // Invalid duration
        training.setTrainingDuration(0);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(training));
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