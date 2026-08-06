package org.bugra.mapper;

import org.bugra.dto.response.TraineeTrainings;
import org.bugra.dto.response.TrainerTrainings;
import org.bugra.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingResponseMapperTest {

    TrainingResponseMapper trainingResponseMapper = new TrainingResponseMapper();

    private Training validTrainingTrainer(String name, String type, LocalDate date, int duration, String trainerUsername) {
        User user = new User();
        user.setUsername(trainerUsername);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(type);

        Training training = new Training();
        training.setTrainingName(name);
        training.setTrainingType(trainingType);
        training.setTrainingDate(date);
        training.setTrainingDuration(duration);
        training.setTrainer(trainer);

        return training;
    }

    private Training validTrainingTrainee(String name, String type, LocalDate date, int duration, String traineeUsername) {
        User user = new User();
        user.setUsername(traineeUsername);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(type);

        Training training = new Training();
        training.setTrainingName(name);
        training.setTrainingType(trainingType);
        training.setTrainingDate(date);
        training.setTrainingDuration(duration);
        training.setTrainee(trainee);

        return training;
    }

    @Test
    @DisplayName("Should map List<Training> to List<TraineeTrainings> correctly")
    void mapToTraineeTrainings_shouldMapCorrectly() {
        LocalDate now = LocalDate.now();
        Training training = validTrainingTrainer("Chest", "STRENGTH", now, 60, "arnold.fit");

        List<TraineeTrainings> responseList = trainingResponseMapper.mapToTraineeTrainings(List.of(training));

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        TraineeTrainings response = responseList.get(0);
        assertEquals("Chest", response.trainingName());
        assertEquals("STRENGTH", response.trainingType().getTrainingTypeName());
        assertEquals(now, response.date());
        assertEquals(60, response.duration());
        assertEquals("arnold.fit", response.trainerName());
    }

    @Test
    @DisplayName("Should return empty list when input trainings list is empty")
    void mapToTraineeTrainings_shouldHandleEmptyList() {
        List<TraineeTrainings> responseList = trainingResponseMapper.mapToTraineeTrainings(List.of());

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }

    @Test
    @DisplayName("Should map multiple trainings correctly maintaining size and order")
    void mapToTraineeTrainings_shouldMapMultipleTrainings() {
        LocalDate date1 = LocalDate.of(2026, 7, 1);
        LocalDate date2 = LocalDate.of(2026, 7, 15);

        Training training1 = validTrainingTrainer("Leg Day", "STRENGTH", date1, 75, "ronnie.fit");
        Training training2 = validTrainingTrainer("Cardio", "CARDIO", date2, 45, "zyzz.coach");

        List<TraineeTrainings> responseList = trainingResponseMapper.mapToTraineeTrainings(List.of(training1, training2));

        assertNotNull(responseList);
        assertEquals(2, responseList.size());

        assertEquals("Leg Day", responseList.get(0).trainingName());
        assertEquals("ronnie.fit", responseList.get(0).trainerName());

        assertEquals("Cardio", responseList.get(1).trainingName());
        assertEquals("zyzz.coach", responseList.get(1).trainerName());
    }

    @Test
    @DisplayName("Should map List<Training> to List<TrainerTrainings> correctly")
    void mapToTrainerTrainings_shouldMapCorrectly() {
        LocalDate now = LocalDate.now();
        Training training = validTrainingTrainee("Chest", "STRENGTH", now, 60, "jane.smith");

        List<TrainerTrainings> responseList = trainingResponseMapper.mapToTrainerTrainings(List.of(training));

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        TrainerTrainings response = responseList.get(0);
        assertEquals("Chest", response.trainingName());
        assertEquals("STRENGTH", response.trainingType().getTrainingTypeName());
        assertEquals(now, response.date());
        assertEquals(60, response.duration());
        assertEquals("jane.smith", response.traineeName());
    }

    @Test
    @DisplayName("Should return empty list when input trainings list is empty")
    void mapToTrainerTrainings_shouldHandleEmptyList() {
        List<TrainerTrainings> responseList = trainingResponseMapper.mapToTrainerTrainings(List.of());

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }

    @Test
    @DisplayName("Should map multiple trainings correctly maintaining size and order")
    void mapToTrainerTrainings_shouldMapMultipleTrainings() {
        LocalDate date1 = LocalDate.of(2026, 7, 1);
        LocalDate date2 = LocalDate.of(2026, 7, 15);


        Training training1 = validTrainingTrainee("Leg Day", "STRENGTH", date1, 75, "jane.smith");
        Training training2 = validTrainingTrainee("Cardio", "CARDIO", date2, 45, "john.doe");

        List<TrainerTrainings> responseList = trainingResponseMapper.mapToTrainerTrainings(List.of(training1, training2));

        assertNotNull(responseList);
        assertEquals(2, responseList.size());

        assertEquals("Leg Day", responseList.get(0).trainingName());
        assertEquals("jane.smith", responseList.get(0).traineeName());

        assertEquals("Cardio", responseList.get(1).trainingName());
        assertEquals("john.doe", responseList.get(1).traineeName());
    }
}