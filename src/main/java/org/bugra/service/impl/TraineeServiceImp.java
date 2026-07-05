package org.bugra.service.impl;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.service.TraineeService;
import org.bugra.service.UserCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImp implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImp.class);
    private TraineeRepo traineeRepo;
    private UserCredentialsService userCredentialsService;


    @Override
    public Trainee createTrainee(Trainee trainee) {
        if (trainee == null || trainee.getUser() == null) {
            logger.error("Attempted to create a null trainee or trainee without user credentials");
            throw new IllegalArgumentException("Trainee and associated User cannot be null");
        }

        User user = trainee.getUser();

        // Generate random password
        String password = userCredentialsService.generateRandomPassword();
        user.setPassword(password);

        // Generate username
        String finalUsername = userCredentialsService.generateUsername(
                user.getFirstName(),
                user.getLastName()
        );

        user.setUsername(finalUsername);
        user.setActive(true);
        user.setRole(UserRole.TRAINEE);

        Trainee savedTrainee = traineeRepo.save(trainee);

        logger.info("Trainee created successfully with ID: {} and username: {}",
                savedTrainee.getId(),
                savedTrainee.getUser().getUsername());

        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        // Null check for object and id
        if (trainee == null ) {
            logger.error("Update failed: Trainee or ID is null");
            throw new IllegalArgumentException("Trainee or Trainee ID cannot be null");
        }

        logger.info("Trainee updated successfully with ID: {}", trainee.getId());
        return traineeRepo.update(trainee)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found with id: " + trainee.getId()));
    }

    @Override
    public boolean deleteTraineeById(long traineeId) {
        if (!traineeRepo.deleteById(traineeId)) {
            throw new UserNotFoundException("Trainee not found with id: " + traineeId);
        }
        logger.info("Trainee with id: {} deleted successfully", traineeId );
        return true;
    }

    @Override
    public boolean deleteTraineeByUsername(String username) {
        if(username == null){
            throw new IllegalArgumentException("Username can not be null");
        }

        logger.info("Deleting the trainee with the username: {} ", username);
        if (!userCredentialsService.deleteByUsername()) {
            throw new UserNotFoundException("Trainee not found with id: " + traineeId);
        }


    }

    @Override
    public Trainee getTrainee(long traineeId) {

        // fetch user and throw exception if it returns empty
        Trainee  trainee = traineeRepo.findById(traineeId)
                .orElseThrow(() -> {
                    logger.warn("User with id: {} not found", traineeId);
                    return new UserNotFoundException("Trainee not found with id: " + traineeId);
                });

        logger.info("User with id: {} and username: {} fetched",
                traineeId,
                trainee.getUser().getUsername());

        return trainee;
    }


    @Override
    public boolean existsById(long id){
        return traineeRepo.existsById(id);
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
