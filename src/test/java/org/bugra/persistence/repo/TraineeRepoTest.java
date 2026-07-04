package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.bugra.model.User;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeRepoTest  extends BaseJpaTest {

    TraineeRepo fakeRepo;

    @BeforeEach
    void setup(){
        fakeRepo = new TraineeRepo();
        fakeRepo.setEntityManager(em);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when entity is null")
    void save_shouldThrowWhenEntityNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fakeRepo.save(null));
    }

    @Test
    @DisplayName("Should return entity when save is successful")
    void save_shouldReturnEntityIfSaveSuccessful() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        trainee.getUser().setUsername("test");

        Trainee res = fakeRepo.save(trainee);

        assertNotNull(res);
        assertEquals("test", res.getUser().getUsername());
    }

    @Test
    @DisplayName("Should return Optional.empty when user does not exists")
    void updateById_shouldReturnEmptyIfNotExist() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        Optional<Trainee> result = fakeRepo.update(trainee);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return updated entity when update is successful")
    void updateById_shouldReturnUpdatedEntity() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("John");
        trainee.setUser(user);
        Trainee savedTrainee = fakeRepo.save(trainee);
        em.flush();
        em.clear();

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(savedTrainee.getId());

        User updatedUser = new User();
        updatedUser.setId(savedTrainee.getUser().getId());
        updatedUser.setFirstName("Mike");

        updatedTrainee.setUser(updatedUser);

        Optional<Trainee> result = fakeRepo.update(updatedTrainee);

        assertTrue(result.isPresent());
        assertEquals("Mike", result.get().getUser().getFirstName());
    }
}