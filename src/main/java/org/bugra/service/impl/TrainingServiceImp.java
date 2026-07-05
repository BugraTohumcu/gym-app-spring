package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.service.TraineeService;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TrainingServiceImp implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImp.class);
    private TrainingRepo trainingRepo;
    private TrainingTypeRepo trainingTypeRepo;

    @Override
    public Training createTraining(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }

        String trainingTypeName = training.getTrainingType().getTrainingTypeName();

        TrainingType type = trainingTypeRepo.findByTrainingTypeName(trainingTypeName)
                .orElseGet(() -> {
                    logger.info("New TrainingType found, creating: {}", trainingTypeName);
                    TrainingType newType = new TrainingType();
                    newType.setTrainingTypeName(trainingTypeName);
                    return trainingTypeRepo.save(newType);
                });


        training.setTrainingType(type);

        if (training.getTrainee() == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (training.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

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
        if (date == null) {
            logger.error("Training date cannot be null");
            throw new IllegalArgumentException("Training date cannot be null");
        }

        // Invalid duration check
        if (duration <= 0) {
            logger.error("Training duration must be positive");
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    @Override
    public List<Training> getTrainerTrainings(TrainerTrainingFilter filter) {
        return trainingRepo.findByTrainerCriteria(filter);
    }

    @Override
    public List<Training> getTraineeTrainings(TraineeTrainingFilter filter) {
        return trainingRepo.findByTraineeCriteria(filter);
    }

    @Autowired
    public void setTrainingRepo(TrainingRepo trainingRepo) {
        this.trainingRepo = trainingRepo;
    }

    @Autowired
    public void setTrainingTypeRepo(TrainingTypeRepo trainingTypeRepo) {
        this.trainingTypeRepo = trainingTypeRepo;
    }
}