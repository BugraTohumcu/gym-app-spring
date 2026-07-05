package org.bugra.persistence.repo;

import org.bugra.enums.UserRole;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerRepoTest extends BaseJpaTest {

    private TrainerRepo trainerRepo;

    @BeforeEach
    void init() {
        trainerRepo = new TrainerRepo();
        trainerRepo.setEntityManager(em);
    }

    private Trainer createAndSaveTrainer(String username) {
        User user = createValidUser(username, UserRole.TRAINER);
        em.persist(user);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        em.persist(trainer);
        return trainer;
    }

    @Test
    @DisplayName("Should return all trainers when trainee has no assignments")
    void findAllNotAssignedToTrainee_shouldReturnAllTrainersWhenNoAssignments() {
        User user = createValidUser("john.doe", UserRole.TRAINEE);
        em.persist(user);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setTrainers(new HashSet<>());
        em.persist(trainee);

        createAndSaveTrainer("trainer.one");
        createAndSaveTrainer("trainer.two");

        em.flush();
        em.clear();

        List<Trainer> result = trainerRepo.findAllNotAssignedToTrainee("john.doe");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username is null")
    void findAllNotAssignedToTrainee_shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerRepo.findAllNotAssignedToTrainee(null));
    }
}