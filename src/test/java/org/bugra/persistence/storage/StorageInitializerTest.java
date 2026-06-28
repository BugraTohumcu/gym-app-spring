package org.bugra.persistence.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.bugra.mapper.StorageMapper;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageInitializerTest {

    @Mock
    private StorageMapper storageMapper;
    private StorageInitializer storageInitializer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageInitializer = new StorageInitializer(storageMapper, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Test
    @DisplayName("Should successfully load all data into maps when files exist")
    void shouldLoadAllDataSuccessfully() throws IOException {
        // Create temporary files with sample rows
        Path trainerFile = tempDir.resolve("trainers.csv");
        Path traineeFile = tempDir.resolve("trainees.csv");
        Path trainingFile = tempDir.resolve("trainings.csv");

        Files.writeString(trainerFile, "trainer-row-1\n");
        Files.writeString(traineeFile, "trainee-row-1\n");
        Files.writeString(trainingFile, "training-row-1\n");

        // Mocking models
        Trainer mockTrainer = new Trainer();
        mockTrainer.setId(100L);

        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(200L);

        Training mockTraining = new Training();
        mockTraining.setId(300L);

        when(storageMapper.parseTrainer("trainer-row-1")).thenReturn(mockTrainer);
        when(storageMapper.parseTrainee("trainee-row-1")).thenReturn(mockTrainee);
        when(storageMapper.parseTraining("training-row-1")).thenReturn(mockTraining);

        Map<Long, Trainer> trainerStorage = new HashMap<>();
        Map<Long, Trainee> traineeStorage = new HashMap<>();
        Map<Long, Training> trainingStorage = new HashMap<>();

        storageInitializer = new StorageInitializer(storageMapper, trainerStorage, traineeStorage, trainingStorage);
        ReflectionTestUtils.setField(storageInitializer, "trainerPath", trainerFile.toString());
        ReflectionTestUtils.setField(storageInitializer, "traineePath", traineeFile.toString());
        ReflectionTestUtils.setField(storageInitializer, "trainingPath", trainingFile.toString());

        storageInitializer.init();

        // Verify maps are populated correctly
        assertEquals(1, trainerStorage.size());
        assertTrue(trainerStorage.containsKey(100L));
        assertSame(mockTrainer, trainerStorage.get(100L));

        assertEquals(1, traineeStorage.size());
        assertTrue(traineeStorage.containsKey(200L));
        assertSame(mockTrainee, traineeStorage.get(200L));

        assertEquals(1, trainingStorage.size());
        assertTrue(trainingStorage.containsKey(300L));
        assertSame(mockTraining, trainingStorage.get(300L));

        // Verify interactions
        verify(storageMapper, times(1)).parseTrainer("trainer-row-1");
        verify(storageMapper, times(1)).parseTrainee("trainee-row-1");
        verify(storageMapper, times(1)).parseTraining("training-row-1");
    }

    @Test
    @DisplayName("Should handle IOException gracefully and leave maps empty when files do not exist")
    void shouldHandleFileNotFoundGracefully() {

        String invalidPath = "non_exist/file.csv";

        Map<Long, Trainer> trainerStorage = new HashMap<>();
        Map<Long, Trainee> traineeStorage = new HashMap<>();
        Map<Long, Training> trainingStorage = new HashMap<>();

        storageInitializer = new StorageInitializer(storageMapper, trainerStorage, traineeStorage, trainingStorage);
        ReflectionTestUtils.setField(storageInitializer, "trainerPath", invalidPath);
        ReflectionTestUtils.setField(storageInitializer, "traineePath", invalidPath);
        ReflectionTestUtils.setField(storageInitializer, "trainingPath", invalidPath);

        assertDoesNotThrow(() -> storageInitializer.init());

        // Verify that maps remain empty
        assertTrue(trainerStorage.isEmpty());
        assertTrue(traineeStorage.isEmpty());
        assertTrue(trainingStorage.isEmpty());

        // Verify that mapper was never called since files could not be opened
        verifyNoInteractions(storageMapper);
    }
}