package org.bugra.config;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StorageConfigTest {

    private StorageConfig storageConfig;

    @BeforeEach
    void setUp() {
        storageConfig = new StorageConfig();
    }


    @Test
    @DisplayName("should return a non-null empty map")
    void trainerStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Trainer> storage = storageConfig.trainerStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    @DisplayName("should return a non-null empty map")
    void traineeStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Trainee> storage = storageConfig.traineeStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    @DisplayName("should return a non-null empty map")
    void trainingStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Training> storage = storageConfig.trainingStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }
}