package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.enums.UserRole;
import org.bugra.exception.TrainingTypeNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.service.TrainerService;
import org.bugra.service.UserCredentialsService;
import org.bugra.util.TrainerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private TrainerRepo trainerRepo;
    private UserCredentialsService userCredentialsService;
    private TrainingTypeRepo trainingTypeRepo;

    @Transactional
    @Override
    public Trainer createTrainer(Trainer trainer) {
        TrainerValidator.validate(trainer);

        String trainingTypeName = trainer.getSpecialization().getTrainingTypeName();

        TrainingType type = trainingTypeRepo.findByTrainingTypeName(trainingTypeName)
                .orElseThrow(TrainingTypeNotFoundException::new);


        trainer.setSpecialization(type);

        User user = trainer.getUser();

        // Credentials generation
        user.setPassword(userCredentialsService.generateRandomPassword());
        user.setUsername(userCredentialsService.generateUsername(
                user.getFirstName(),
                user.getLastName()
        ));

        user.setActive(true);
        user.setRole(UserRole.TRAINER);

        Trainer savedTrainer = trainerRepo.save(trainer);
        logger.info("Trainer created successfully with ID: {} and username: {}",
                savedTrainer.getId(),
                savedTrainer.getUser().getUsername());

        return savedTrainer;
    }

    @Transactional
    @Override
    public Trainer updateTrainer(Trainer trainer) {
        TrainerValidator.validate(trainer);

        Trainer updated = trainerRepo.update(trainer)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with id: " + trainer.getId()));

        logger.info("Trainer updated successfully with ID: {}", trainer.getId());
        return updated;
    }

    @Transactional
    @Override
    public Trainer getTrainer(long trainerId) {
        Trainer trainer = trainerRepo.findById(trainerId)
                .orElseThrow(() -> {
                    logger.warn("Trainer with id: {} not found", trainerId);
                    return new UserNotFoundException("Trainer not found with id: " + trainerId);
                });

        logger.info("Trainer with id: {} successfully fetched", trainerId); // Bunu ekleyebilirsin
        return trainer;
    }


    @Transactional
    @Override
    public boolean existsById(long id) {
        return trainerRepo.existsById(id);
    }

    @Transactional
    @Override
    public Trainer getTrainerByUsername(String username) {
        return trainerRepo.findTrainerByUsername(username);
    }

    @Transactional
    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username cannot be null or blank");
        }
        return trainerRepo.findAllNotAssignedToTrainee(traineeUsername);
    }

    @Autowired
    public void setTrainerRepo(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Autowired
    public void setTrainingTypeRepo(TrainingTypeRepo trainingTypeRepo) {
        this.trainingTypeRepo = trainingTypeRepo;
    }

    @Autowired
    public void setUserCredentialsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }
}