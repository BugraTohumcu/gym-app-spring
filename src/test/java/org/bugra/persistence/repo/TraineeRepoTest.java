package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TraineeRepoTest {

    Map<Long, Trainee> fakeStorage;
    TraineeRepo fakeRepo;

    @BeforeEach
    void setup(){
        fakeStorage = new ConcurrentHashMap<>();
        fakeRepo = new TraineeRepo(fakeStorage);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when entity is null")
    void save_shouldThrowWhenEntityNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fakeRepo.save(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when id is null")
    void save_shouldThrowWhenIdNull() {
        Trainee trainee = new Trainee();

        assertThrows(IllegalArgumentException.class,
                () -> fakeRepo.save(trainee));
    }

    @Test
    @DisplayName("Should return entity when save is successful")
    void save_shouldReturnEntityIfSaveSuccessful() {
        long id = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(id);

        Trainee res = fakeRepo.save(trainee);

        assertNotNull(res);
        assertEquals(id, res.getId());
    }

    @Test
    @DisplayName("Should return Optional.empty when user does not exists")
    void updateById_shouldReturnEmptyIfNotExist() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        Optional<Trainee> result = fakeRepo.updateById(trainee);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return updated entity when update is successful")
    void updateById_shouldReturnUpdatedEntity() {
        long id = 1L;
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setId(id);
        fakeStorage.put(id, trainee);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setId(id);
        updatedTrainee.setFirstName("Mike");
        Optional<Trainee> result = fakeRepo.updateById(updatedTrainee);

        assertTrue(result.isPresent());
        assertEquals(updatedTrainee.getFirstName(), result.get().getFirstName());
    }

    @Test
    @DisplayName("Should return false when username is null")
    void existsByUsername_shouldReturnFalseWhenUsernameNull() {
        boolean result = fakeRepo.existsByUsername(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when username exists")
    void existsByUsername_shouldReturnTrueWhenUsernameExists() {
        long id = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUsername("John.Doe1");
        fakeStorage.put(id, trainee);

        boolean result = fakeRepo.existsByUsername(trainee.getUsername());
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when username does not exists")
    void existsByUsername_shouldReturnFalseWhenUsernameNotExists() {
        String username = "John.Doe1";

        boolean result = fakeRepo.existsByUsername(username);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return 0 when storage is empty")
    void getMaxId_shouldReturnZeroWhenStorageEmpty(){
        assertEquals(0L, fakeRepo.getMaxId());
    }

    @Test
    @DisplayName("Should return highest id in storage")
    void getMaxId_shouldReturnHighestId(){
        for (long i = 1; i <= 3; i++) {
            Trainee t = new Trainee();
            t.setId(i);
            fakeRepo.save(t);
        }
        assertEquals(3L, fakeRepo.getMaxId());
    }
}