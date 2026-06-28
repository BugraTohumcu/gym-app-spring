package org.bugra.service;

import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.persistence.repo.TraineeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImp implements TraineeService{

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImp.class);
    private final TraineeRepo traineeRepo;

    public TraineeServiceImp(TraineeRepo traineeRepo) {
        this.traineeRepo = traineeRepo;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        return null;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        return null;
    }

    @Override
    public boolean deleteTrainee(long traineeId) {
        return false;
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

    @Override
    public String generateUsername(String firstName, String lastName) {
        // Null and blank check for credentials
        if (firstName == null || lastName == null ||
                firstName.isBlank() || lastName.isBlank()) {
            logger.warn("First name or last name cannot be null or empty");
            throw new IllegalArgumentException("First name or last name cannot be null or empty");
        }

        // Create base username
        String baseName = (firstName.trim() + "." + lastName.trim()).toLowerCase();
        StringBuilder builder = new StringBuilder(baseName);

        // Check if username is taken
        // For the simplicity I've used linear search.
        // Better approach may be second level indexing eg: {"john.doe": 0}
        int counter = 1;
        while (traineeRepo.existsByUsername(builder.toString())) {
            // Reset username
            builder.setLength(0);

            builder.append(baseName).append(counter);
            counter++;
        }

        return builder.toString();
    }

    @Override
    public String generateRandomPassword() {
        return "";
    }
}
