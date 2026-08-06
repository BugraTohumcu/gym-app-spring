package org.bugra.mapper;

import org.bugra.dto.response.TrainerProfileResponse;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerResponseMapperTest {

    TrainerResponseMapper trainerResponseMapper = new TrainerResponseMapper();

    private Trainer validTrainer() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    private Trainee validTrainee(String username, String firstName, String lastName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    @DisplayName("Should map Trainer to TrainerProfileResponse correctly")
    void mapToTrainerProfileResponse_shouldMapCorrectly() {
        Trainer trainer = validTrainer();

        Trainee trainee = validTrainee("john.doe", "John", "Doe");
        trainer.setTrainees(Set.of(trainee));

        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);

        assertNotNull(response);
        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
        assertTrue(response.isActive());
        assertEquals(1, response.trainees().size());

        TrainerProfileResponse.TraineeListSummary summary = response.trainees().get(0);
        assertEquals("john.doe", summary.userName());
        assertEquals("John", summary.firstName());
        assertEquals("Doe", summary.lastName());
    }

    @Test
    @DisplayName("Should return empty list when trainer has no trainees")
    void mapToTrainerProfileResponse_shouldHandleEmptyTrainees() {
        Trainer trainer = validTrainer();
        trainer.setTrainees(Set.of());

        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);

        assertTrue(response.trainees().isEmpty());
    }

    @Test
    @DisplayName("Should map isActive false correctly")
    void mapToTrainerProfileResponse_shouldMapInactiveTrainer() {
        Trainer trainer = validTrainer();
        trainer.getUser().setActive(false);
        trainer.setTrainees(Set.of());

        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);

        assertFalse(response.isActive());
    }
}