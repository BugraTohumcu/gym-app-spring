package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.service.TraineeService;
import org.bugra.service.UserCredentialsService;
import org.bugra.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class TraineeServiceImp implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImp.class);
    private TraineeRepo traineeRepo;
    private UserCredentialsService userCredentialsService;
    private UserService userService;


    @Transactional
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

    @Transactional
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

    @Transactional
    @Override
    public boolean deleteTraineeById(long traineeId) {
        if (!traineeRepo.deleteById(traineeId)) {
            throw new UserNotFoundException("Trainee not found with id: " + traineeId);
        }
        logger.info("Trainee with id: {} deleted successfully", traineeId );
        return true;
    }

    @Transactional
    @Override
    public boolean deleteTraineeByUsername(String username) {
        return userService.deleteByUsername(username);
    }

    @Transactional
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

    @Transactional
    @Override
    public boolean existsById(long id){
        return traineeRepo.existsById(id);
    }

    @Transactional
    @Override
    public Trainee getTraineeByUsername(String username) {
        return traineeRepo.findTraineeByUsername(username);
    }

    @Transactional
    @Override
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        traineeRepo.updateTraineeTrainers(traineeUsername, trainerUsernames);
    }

    @Autowired
    public void setTraineeRepo(TraineeRepo traineeRepo) {
        this.traineeRepo = traineeRepo;
    }

    @Autowired
    public void setUserCredentialsService(UserCredentialsService userCredentialsService){
        this.userCredentialsService = userCredentialsService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
