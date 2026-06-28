package org.bugra.service;

import org.bugra.exception.TrainingNotFoundException;
import org.bugra.model.Training;
import org.bugra.persistence.repo.TrainingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingServiceImp implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImp.class);
    private TrainingRepo trainingRepo;

    @Override
    public Training createTraining(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }

        // Duration and training date check
        validateTrainingDateAndDuration(
                training.getTrainingDate(),
                training.getTrainingDuration()
        );

        Training savedTraining = trainingRepo.save(training);
        logger.info("Training created successfully with ID: {}", savedTraining.getId());
        return savedTraining;
    }

    @Override
    public Training getTraining(long trainingId) {
        Training training = trainingRepo.findById(trainingId)
                .orElseThrow(() -> {
                    logger.warn("Training with id: {} not found", trainingId);
                    return new TrainingNotFoundException("Training not found with id: " + trainingId);
                });

        logger.info("Training with id: {} and name: '{}' successfully fetched",
                trainingId, training.getTrainingName());
        return training;
    }

    @Override
    public void validateTrainingDateAndDuration(LocalDate date, int duration) {
        // Null and invalid time check
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Training date cannot be null or in the past");
        }

        // Invalid duration check
        if (duration <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    @Autowired
    public void setTrainingRepo(TrainingRepo trainingRepo) {
        this.trainingRepo = trainingRepo;
    }
}