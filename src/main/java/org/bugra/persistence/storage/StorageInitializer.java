package org.bugra.persistence.storage;

import org.bugra.mapper.StorageMapper;
import org.bugra.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageInitializer {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);
    private final StorageMapper mapper;

    public StorageInitializer(StorageMapper mapper) {
        this.mapper = mapper;
    }

    // Load data before program starts
    public void loadAllData(Map<Long, Trainer> trainerStorage,
                            Map<Long, Trainee> traineeStorage,
                            Map<Long, Training> trainingStorage,
                            String trainerPath,
                            String traineePath,
                            String trainingPath) {
        logger.info("Storage initialization is started.");
        loadData(trainerStorage, trainerPath, mapper::parseTrainer, Trainer::getId);
        loadData(traineeStorage, traineePath, mapper::parseTrainee, Trainee::getId);
        loadData(trainingStorage, trainingPath, mapper::parseTraining, Training::getTraineeId);
        logger.info("Storage initialized from files.");
    }


    // Generic data loading
     private <V> void loadData(Map<Long, V> dataMap,
                               String filePath,
                               Function<String, V> parseData,
                               Function<V, Long> keyExtractor) {

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            java.lang.String line;
            while ((line = reader.readLine()) != null){
                V entity = parseData.apply(line);
                dataMap.put(keyExtractor.apply(entity), entity);
            }
        } catch (IOException e) {
            logger.warn("Data is not found starting empty");
        }
    }

}
