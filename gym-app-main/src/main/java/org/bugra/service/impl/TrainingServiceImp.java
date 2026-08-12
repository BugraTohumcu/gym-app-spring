package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bugra.client.WorkloadClientFacade;
import org.bugra.dto.request.CreateTraining;
import org.bugra.dto.request.TraineeTrainingFilter;
import org.bugra.dto.request.TrainerTrainingFilter;
import org.bugra.enums.ActionType;
import org.bugra.exception.TrainingNotFoundException;
import org.bugra.exception.TrainingTypeNotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImp implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImp.class);
    private final TrainingRepo trainingRepo;
    private final TrainingTypeRepo trainingTypeRepo;
    private final TraineeRepo traineeRepo;
    private final TrainerRepo trainerRepo;
    private final WorkloadClientFacade workloadClient;


    @Override
    public Training createTraining(CreateTraining createTraining) {

        // create the specialization if it does not exist
        TrainingType type = trainingTypeRepo.findByTrainingTypeName(createTraining.trainingTypeName())
                .orElseThrow(TrainingTypeNotFoundException::new);

        Trainee managedTrainee = traineeRepo.findTraineeByUsername(createTraining.traineeUsername())
                .orElseThrow(UserNotFoundException::new);

        Trainer managedTrainer = trainerRepo.findTrainerByUsername(createTraining.trainerUsername());

        // Update trainee_trainer table
        managedTrainer.getTrainees().add(managedTrainee);
        managedTrainee.getTrainers().add(managedTrainer);

        Training training = new Training();
        training.setTrainer(managedTrainer);
        training.setTrainee(managedTrainee);
        training.setTrainingName(createTraining.trainingName());
        training.setTrainingType(type);
        training.setTrainingDate(createTraining.trainingDate());
        training.setTrainingDuration(createTraining.trainingDuration());

        workloadClient.sendWorkload(
                workloadClient.buildWorkloadRequest(training, ActionType.ADD)
        );
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
}