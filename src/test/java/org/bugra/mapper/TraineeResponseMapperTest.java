package org.bugra.mapper;

import org.bugra.dto.response.TraineeProfileResponse;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TraineeResponseMapperTest {


    TraineeResponseMapper traineeResponseMapper = new TraineeResponseMapper();

    private Trainee validTrainee(Long id) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(user);
        return trainee;
    }

    @Test
    @DisplayName("Should map Trainee to TraineeProfileResponse correctly")
    void mapToTraineeProfileResponse_shouldMapCorrectly() {
        Trainee trainee = validTrainee(1L);
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainer.setUser(trainerUser);

        TrainingType spec = new TrainingType();
        spec.setTrainingTypeName("Java");
        trainer.setSpecialization(spec);

        trainee.setTrainers(Set.of(trainer));

        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);

        assertNotNull(response);
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals(1, response.trainers().size());

        TraineeProfileResponse.TrainerSummary summary = response.trainers().get(0);
        assertEquals("Jane", summary.firstName());
        assertEquals("Smith", summary.lastName());
        assertEquals("Java", summary.specialization());
    }

    @Test
    @DisplayName("Should return empty list when trainee has no trainers")
    void mapToTraineeProfileResponse_shouldHandleEmptyTrainers() {
        Trainee trainee = validTrainee(1L);
        trainee.setTrainers(Set.of());

        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);

        assertTrue(response.trainers().isEmpty());
    }

}