package org.bugra.service.impl;

import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Training;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.service.TraineeService;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingServiceImp implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImp.class);
    private TrainingRepo trainingRepo;
    private TraineeService traineeService;
    private TrainerService trainerService;

    @Override
    public Training createTraining(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }

        // Check if trainer id exist
        {
            long traineeId = training.getTrainee().getId();
            if (!traineeService.existsById(traineeId)) {
                logger.error("The trainee with id {} not found for training with id {}",
                        traineeId,
                        training.getId());

                throw new UserNotFoundException("Trainee is not found with id: " +
                        traineeId);
            }
        }

        // Check if trainer id exist
        {
            long trainerId = training.getTrainer().getId();
            if (!trainerService.existsById(trainerId)) {
                logger.error("The trainer with id {} not found for training with id {}",
                        trainerId,
                        training.getId());

                throw new UserNotFoundException("Trainer is not found with id: " +
                        trainerId);
            }
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
            logger.error("Training date cannot be null or in the past");
            throw new IllegalArgumentException("Training date cannot be null or in the past");
        }

        // Invalid duration check
        if (duration <= 0) {
            logger.error("Training duration must be positive");
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    @Autowired
    public void setTrainingRepo(TrainingRepo trainingRepo) {
        this.trainingRepo = trainingRepo;
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }
}