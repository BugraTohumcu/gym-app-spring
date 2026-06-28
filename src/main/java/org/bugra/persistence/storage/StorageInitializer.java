package org.bugra.persistence.storage;

import jakarta.annotation.PostConstruct;
import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.mapper.StorageMapper;
import org.bugra.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;import java.util.Map;

@Component
public class StorageInitializer {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    // Storage
    private final StorageMapper mapper;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Training> trainingStorage;

    // Data Paths
    @Value("${storage.trainers.file}") private String trainerPath;
    @Value("${storage.trainees.file}") private String traineePath;
    @Value("${storage.trainings.file}") private String trainingPath;

    public StorageInitializer(
            StorageMapper mapper,
            @StorageQualifier(StorageType.TRAINER) Map<Long, Trainer> trainerStorage,
            @StorageQualifier(StorageType.TRAINEE) Map<Long, Trainee> traineeStorage,
            @StorageQualifier(StorageType.TRAINING) Map<Long, Training> trainingStorage) {
        this.mapper = mapper;
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
    }

    // Load data before program starts
    @PostConstruct
    public void init() {
        logger.info("Storage initialization is started.");
        loadData(trainerStorage, trainerPath, mapper::parseTrainer, Trainer::getId);
        loadData(traineeStorage, traineePath, mapper::parseTrainee, Trainee::getId);
        loadData(trainingStorage, trainingPath, mapper::parseTraining, Training::getId);
        logger.info("Storage initialized from files.");
    }

    // Generic data loading
    private <V> void loadData(Map<Long, V> dataMap,
                              String filePath,
                              Function<String, V> parseData,
                              Function<V, Long> keyExtractor) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                V entity = parseData.apply(line);

                if (entity != null) {
                    Long key = keyExtractor.apply(entity);
                    if (key != null) {
                        dataMap.put(key, entity);
                    } else {
                        logger.warn("Skipping entity with null ID in file: {}", filePath);
                    }
                } else {
                    logger.warn("Skipping unparseable line in file: {}", filePath);
                }
            }
        } catch (IOException e) {
            logger.warn("Data file not found or unreadable: {}. Starting with empty storage.", filePath);
        }
    }

}
