package org.bugra.config;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.persistence.storage.StorageInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageConfigTest {

    @Mock
    private StorageInitializer storageInitializer;

    private StorageConfig storageConfig;

    @BeforeEach
    void setUp() {
        storageConfig = new StorageConfig(storageInitializer);

        // Inject @Value fields manually
        ReflectionTestUtils.setField(storageConfig, "trainersPath", "data/trainers.csv");
        ReflectionTestUtils.setField(storageConfig, "traineesPath", "data/trainees.csv");
        ReflectionTestUtils.setField(storageConfig, "trainingsPath", "data/trainings.csv");
    }


    @Test
    @DisplayName("trainerStorage() should return a non-null empty map")
    void trainerStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Trainer> storage = storageConfig.trainerStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    @DisplayName("traineeStorage() should return a non-null empty map")
    void traineeStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Trainee> storage = storageConfig.traineeStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    @DisplayName("trainingStorage() should return a non-null empty map")
    void trainingStorageShouldReturnEmptyNonNullMap() {
        Map<Long, Training> storage = storageConfig.trainingStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());
    }

    @Test
    @DisplayName("init() should call loadAllData with the correct file paths")
    void initShouldDelegateToStorageInitializerWithCorrectPaths() {
        storageConfig.init();

        verify(storageInitializer, times(1)).loadAllData(
                any(),
                any(),
                any(),
                eq("data/trainers.csv"),
                eq("data/trainees.csv"),
                eq("data/trainings.csv")
        );
    }

    @Test
    @DisplayName("init() called twice should invoke loadAllData twice")
    void initCalledTwiceShouldInvokeLoadAllDataTwice() {
        storageConfig.init();
        storageConfig.init();

        verify(storageInitializer, times(2)).loadAllData(
                any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    @DisplayName("init() should propagate exceptions thrown by StorageInitializer")
    void initShouldPropagateExceptionFromStorageInitializer() {
        doThrow(new RuntimeException("File system error"))
                .when(storageInitializer)
                .loadAllData(any(), any(), any(), any(), any(), any());

        assertThrows(RuntimeException.class, () -> storageConfig.init());
    }
}