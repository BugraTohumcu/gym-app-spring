package org.bugra.service;

import org.bugra.dto.CreateTraining;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.*;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.service.impl.TrainingServiceImp;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImpTest {

    @Mock
    TrainingRepo trainingRepo;

    @Mock
    TrainingTypeRepo trainingTypeRepo;

    @Mock
    TraineeRepo traineeRepo;

    @Mock
    TrainerRepo trainerRepo;

    @InjectMocks
    TrainingServiceImp trainingService;

    private Trainee mockTrainee;
    private Trainer mockTrainer;
    private TrainingType mockType;

    @BeforeEach
    void setUp() {
        mockTrainee = new Trainee();
        mockTrainee.setId(1L);
        mockTrainee.setTrainers(new HashSet<>());

        User trainerUser = new User();
        trainerUser.setUsername("jane.smith");

        mockTrainer = new Trainer();
        mockTrainer.setId(1L);
        mockTrainer.setUser(trainerUser);
        mockTrainer.setTrainees(new HashSet<>());

        mockType = new TrainingType();
        mockType.setId(1L);
        mockType.setTrainingTypeName("Yoga");
    }

    private CreateTraining validRequest() {
        return new CreateTraining(
                "jane.smith",
                1L,
                "Morning Yoga",
                "Yoga",
                LocalDate.now().plusDays(1),
                60
        );
    }


    @Test
    @DisplayName("Should successfully create training when data is valid")
    void createTraining_shouldSaveSuccessfully() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith")).thenReturn(mockTrainer);

        Training saved = new Training();
        saved.setId(1L);
        when(trainingRepo.save(any(Training.class))).thenReturn(saved);

        Training result = trainingService.createTraining(validRequest());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainingRepo, times(1)).save(any(Training.class));
    }

    @Test
    @DisplayName("Should create new TrainingType when it does not exist")
    void createTraining_shouldCreateNewTrainingTypeWhenNotFound() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.empty());
        when(trainingTypeRepo.save(any(TrainingType.class))).thenReturn(mockType);
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith")).thenReturn(mockTrainer);

        Training saved = new Training();
        saved.setId(1L);
        when(trainingRepo.save(any(Training.class))).thenReturn(saved);

        Training result = trainingService.createTraining(validRequest());

        assertNotNull(result);
        verify(trainingTypeRepo, times(1)).save(any(TrainingType.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainee not found")
    void createTraining_shouldThrowWhenTraineeNotFound() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenThrow(new UserNotFoundException("Trainee not found"));

        assertThrows(UserNotFoundException.class,
                () -> trainingService.createTraining(validRequest()));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when trainer not found")
    void createTraining_shouldThrowWhenTrainerNotFound() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith"))
                .thenThrow(new UserNotFoundException("Trainer not found"));

        assertThrows(UserNotFoundException.class,
                () -> trainingService.createTraining(validRequest()));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when date is null")
    void createTraining_shouldThrowWhenDateIsNull() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith")).thenReturn(mockTrainer);

        CreateTraining request = new CreateTraining(
                "jane.smith", 1L, "Morning Yoga", "Yoga", null, 60);

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(request));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when duration is zero")
    void createTraining_shouldThrowWhenDurationZero() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith")).thenReturn(mockTrainer);

        CreateTraining request = new CreateTraining(
                "jane.smith", 1L, "Morning Yoga", "Yoga",
                LocalDate.now().plusDays(1), 0);

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(request));
        verify(trainingRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should add trainee to trainer's list and trainer to trainee's list")
    void createTraining_shouldUpdateBidirectionalRelationship() {
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(mockType));
        when(traineeRepo.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(trainerRepo.findTrainerByUsername("jane.smith")).thenReturn(mockTrainer);
        when(trainingRepo.save(any(Training.class))).thenReturn(new Training());

        trainingService.createTraining(validRequest());

        assertTrue(mockTrainer.getTrainees().contains(mockTrainee));
        assertTrue(mockTrainee.getTrainers().contains(mockTrainer));
    }

    @Test
    @DisplayName("Should return training when found")
    void getTraining_shouldReturnTraining() {
        Training training = new Training();
        training.setTrainingName("Morning Yoga");
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));

        Training result = trainingService.getTraining(1L);

        assertEquals("Morning Yoga", result.getTrainingName());
        verify(trainingRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw TrainingNotFoundException when training not found")
    void getTraining_shouldThrowWhenNotFound() {
        when(trainingRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class,
                () -> trainingService.getTraining(1L));
    }

    @Test
    @DisplayName("Should pass for valid date and duration")
    void validateTrainingDateAndDuration_shouldPassForValidInput() {
        assertDoesNotThrow(() ->
                trainingService.validateTrainingDateAndDuration(
                        LocalDate.now().plusDays(1), 60));
    }

    @Test
    @DisplayName("Should throw for null date")
    void validateTrainingDateAndDuration_shouldThrowForNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.validateTrainingDateAndDuration(null, 60));
    }

    @Test
    @DisplayName("Should throw for zero duration")
    void validateTrainingDateAndDuration_shouldThrowForZeroDuration() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.validateTrainingDateAndDuration(
                        LocalDate.now().plusDays(1), 0));
    }

    @Test
    @DisplayName("Should throw for negative duration")
    void validateTrainingDateAndDuration_shouldThrowForNegativeDuration() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.validateTrainingDateAndDuration(
                        LocalDate.now().plusDays(1), -1));
    }

    @Test
    @DisplayName("Should return trainee trainings list")
    void getTraineeTrainings_shouldReturnList() {
        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "john.doe", null, null, null, null);
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingRepo.findByTraineeCriteria(filter)).thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(filter);

        assertEquals(2, result.size());
        verify(trainingRepo, times(1)).findByTraineeCriteria(filter);
    }

    @Test
    @DisplayName("Should return trainer trainings list")
    void getTrainerTrainings_shouldReturnList() {
        TrainerTrainingFilter filter = new TrainerTrainingFilter(
                "jane.smith", null, null, null);
        List<Training> trainings = List.of(new Training());
        when(trainingRepo.findByTrainerCriteria(filter)).thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings(filter);

        assertEquals(1, result.size());
        verify(trainingRepo, times(1)).findByTrainerCriteria(filter);
    }
}