package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.dto.request.CreateTraining;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.service.TrainingService;
import org.bugra.util.TrainingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingServiceImp implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImp.class);
    private TrainingRepo trainingRepo;
    private TrainingTypeRepo trainingTypeRepo;
    private TraineeRepo traineeRepo;
    private TrainerRepo trainerRepo;


    @Override
    public Training createTraining(CreateTraining createTraining) {
        TrainingValidator.validate(createTraining);

        // create the specialization if it does not exist
        TrainingType type = trainingTypeRepo.findByTrainingTypeName(createTraining.trainingTypeName())
                .orElseGet(() -> {
                    TrainingType newType = new TrainingType();
                    newType.setTrainingTypeName(createTraining.trainingTypeName());
                    return trainingTypeRepo.save(newType);
                });

        Optional<Trainee> managedTrainee = traineeRepo.findById(createTraining.traineeId());


        if(managedTrainee.isEmpty()){
            logger.error("The trainee with id: {} not found", createTraining.traineeId());
            throw new UserNotFoundException("Trainee not found during training creation");
        }

        // Already checks if the user exists
        Trainer managedTrainer = trainerRepo.findTrainerByUsername(createTraining.trainerUsername());

        // Update trainee_trainer table
        managedTrainer.getTrainees().add(managedTrainee.get());
        managedTrainee.get().getTrainers().add(managedTrainer);

        Training training = new Training();
        training.setTrainer(managedTrainer);
        training.setTrainee(managedTrainee.get());
        training.setTrainingName(createTraining.trainingName());
        training.setTrainingType(type);
        training.setTrainingDate(createTraining.trainingDate());
        training.setTrainingDuration(createTraining.trainingDuration());

        Training saved = trainingRepo.save(training);
        logger.info("Training created successfully with ID: {}", saved.getId());
        return saved;
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

    @Autowired
    public void setTraineeRepo(TraineeRepo traineeRepo) {
        this.traineeRepo = traineeRepo;
    }

    @Autowired
    public void setTrainerRepo(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }
}