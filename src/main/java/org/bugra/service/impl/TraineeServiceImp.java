package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.UpdateTrainee;
import org.bugra.dto.response.UserResponse;
import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingRepo;
import org.bugra.service.TraineeService;
import org.bugra.service.UserCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class TraineeServiceImp implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImp.class);
    private TraineeRepo traineeRepo;
    private UserCredentialsService userCredentialsService;
    private TrainingRepo trainingRepo;
    private TrainerRepo trainerRepo;


    @Transactional
    @Override
    public UserResponse createTrainee(RegisterTrainee registerTrainee) {

        User user = new User();
        user.setFirstName(registerTrainee.firstName());
        user.setLastName(registerTrainee.lastName());

        // Generate random password
        String password = userCredentialsService.generateRandomPassword();
        user.setPassword(password);

        // Generate username
        String finalUsername = userCredentialsService.generateUsername(
                registerTrainee.firstName(),
                registerTrainee.lastName()
        );

        user.setUsername(finalUsername);
        user.setActive(true);
        user.setRole(UserRole.TRAINEE);

        // Build trainee
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress(registerTrainee.address());
        trainee.setDateOfBirth(registerTrainee.dateOfBirth());


        Trainee savedTrainee = traineeRepo.save(trainee);

        logger.info("Trainee created successfully with ID: {} and username: {}",
                savedTrainee.getId(),
                savedTrainee.getUser().getUsername());

        return new UserResponse(
                savedTrainee.getUser().getUsername(),
                password
        );
    }

    @Transactional
    @Override
    public Trainee updateTrainee(UpdateTrainee updateTrainee) {
        Trainee existingTrainee = traineeRepo.findTraineeByUsername(updateTrainee.username());

        existingTrainee.setAddress(updateTrainee.address());
        existingTrainee.setDateOfBirth(updateTrainee.dateOfBirth());

        User user = existingTrainee.getUser();
        user.setFirstName(updateTrainee.firstName());
        user.setLastName(updateTrainee.lastName());
        user.setActive(updateTrainee.isActive());

        logger.info("Trainee updated successfully with id: {}", existingTrainee.getId());
        return traineeRepo.update(existingTrainee)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found with id: " + existingTrainee.getId()));
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
        logger.info("The user with the username: {} is deleting the account", username);

        Trainee trainee = traineeRepo.findTraineeByUsername(username);

        // Delete trainee's trainings
        if(!trainingRepo.deleteByTraineeId(trainee.getId())){
            logger.warn("Not training deleted for trainee with id: {}", trainee.getId());
        }

        // Clean trainer list
        if (trainee.getTrainers() != null) {
            trainee.getTrainers().forEach(trainer -> trainer.getTrainees().remove(trainee));
            trainee.getTrainers().clear();
        }

        return traineeRepo.deleteById(trainee.getId());
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
    public Trainee updateTraineeTrainers(String traineeUsername, List<String> newTrainerUsernames) {
        logger.info("Updating trainers list for trainee: {}", traineeUsername);

        // Trainee check
        Trainee trainee = traineeRepo.findTraineeByUsername(traineeUsername);
        if (trainee == null) {
            throw new UserNotFoundException("Trainee not found with username: " + traineeUsername);
        }

        List<Trainer> foundTrainers = trainerRepo.findAllByUsernames(newTrainerUsernames);

        // Update trainers
        if (foundTrainers.size() != newTrainerUsernames.size()) {
            Set<String> foundUsernames = foundTrainers.stream()
                    .map(t -> t.getUser().getUsername())
                    .collect(Collectors.toSet());

            List<String> missingUsernames = newTrainerUsernames.stream()
                    .filter(username -> !foundUsernames.contains(username))
                    .toList();

            logger.warn("Validation failed. Non-existent trainers: {}", missingUsernames);
            throw new UserNotFoundException("Following trainers not found: " + missingUsernames);
        }

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(foundTrainers);

        return traineeRepo.update(trainee)
                .orElseThrow(UserNotFoundException::new);
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
    public void setTrainingRepo(TrainingRepo trainingRepo) {
        this.trainingRepo = trainingRepo;
    }

    @Autowired
    public void setTrainerRepo(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }
}
