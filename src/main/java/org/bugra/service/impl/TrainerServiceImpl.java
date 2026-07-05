package org.bugra.service.impl;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.service.TrainerService;
import org.bugra.service.UserCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private TrainerRepo trainerRepo;
    private UserCredentialsService userCredentialsService;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

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

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        if (trainer == null) {
            logger.error("Update failed: Trainer or ID is null");
            throw new IllegalArgumentException("Trainer or Trainer ID cannot be null");
        }

        Trainer updated = trainerRepo.update(trainer)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with id: " + trainer.getId()));

        logger.info("Trainer updated successfully with ID: {}", trainer.getId());
        return updated;
    }

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


    @Override
    public boolean existsById(long id) {
        return trainerRepo.existsById(id);
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        return trainerRepo.findTrainerByUsername(username);
    }

    @Autowired
    public void setTrainerRepo(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Autowired
    public void setUserCredentialsService(UserCredentialsService userCredentialsService) {
        this.userCredentialsService = userCredentialsService;
    }
}