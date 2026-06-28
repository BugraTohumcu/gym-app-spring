package org.bugra.config;

import jakarta.annotation.PostConstruct;
import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.persistence.storage.StorageInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "org.bugra")
@PropertySource("classpath:application.properties")
public class StorageConfig {

    @Bean(name = StorageType.Constants.TRAINER_NAME)
    @StorageQualifier(StorageType.TRAINER)
    public Map<Long, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name = StorageType.Constants.TRAINEE_NAME)
    @StorageQualifier(StorageType.TRAINEE)
    public Map<Long, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name = StorageType.Constants.TRAINING_NAME)
    @StorageQualifier(StorageType.TRAINING)
    public Map<Long, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }
}