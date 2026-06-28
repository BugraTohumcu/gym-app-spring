package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.persistence.repo.TraineeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImp implements TraineeService{

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImp.class);
    private TraineeRepo traineeRepo;
    private UserCredentialsService userCredentialsService;


    @Override
    public Trainee createTrainee(Trainee trainee) {
        if (trainee == null) {
            logger.error("Attempted to create a null trainee");
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        // Generate random password
        String password = userCredentialsService.generateRandomPassword();
        trainee.setPassword(password);

        // Generate username
        String finalUsername = userCredentialsService.generateUsername(trainee.getFirstName(),
                trainee.getLastName(),
                traineeRepo::existsByUsername);
        trainee.setUsername(finalUsername);

        Trainee savedTrainee = traineeRepo.save(trainee);

        logger.info("Trainee created successfully with ID: {} and username: {}", savedTrainee.getId(), savedTrainee.getUsername());
        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        // Null check for object and id
        if (trainee == null || trainee.getId() == null) {
            logger.error("Update failed: Trainee or ID is null");
            throw new IllegalArgumentException("Trainee or Trainee ID cannot be null");
        }

        logger.info("Trainee updated successfully with ID: {}", trainee.getId());
        return traineeRepo.updateById(trainee)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found with id: " + trainee.getId()));
    }

    @Override
    public boolean deleteTrainee(long traineeId) {
        if (!traineeRepo.deleteById(traineeId)) {
            throw new UserNotFoundException("Trainee not found with id: " + traineeId);
        }
        return true;
    }

    @Override
    public Trainee getTrainee(long traineeId) {

        // fetch user and throw exception if it returns empty
        Trainee  trainee = traineeRepo.findById(traineeId)
                .orElseThrow(() -> {
                    logger.warn("User with id: {} not found", traineeId);
                    return new UserNotFoundException("Trainee not found with id: " + traineeId);
                });

        logger.info("User with id: {} and username: {} fetched", traineeId, trainee.getUsername());
        return trainee;
    }

    @Autowired
    public void setTraineeRepo(TraineeRepo traineeRepo) {
        this.traineeRepo = traineeRepo;
    }

    @Autowired
    public void setUserCredentialsService(UserCredentialsService userCredentialsService){
        this.userCredentialsService = userCredentialsService;
    }
}
