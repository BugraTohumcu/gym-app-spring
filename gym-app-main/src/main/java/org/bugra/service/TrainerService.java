package org.bugra.service;

import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.request.UpdateTrainer;
import org.bugra.dto.response.UserResponse;
import org.bugra.model.Trainer;

import java.util.List;

/**
 * Service interface for managing {@link Trainer} profiles
 * Provides capabilities to create, update, and retrieve trainer information
 */
public interface TrainerService {

    /**
     * Creates a new trainer profile with auto-generated credentials
     * @param trainer the trainer entity to create
     * @return {@link UserResponse} the created trainer
     */
    UserResponse createTrainer(RegisterTrainer trainer);

    /**
     * Updates an existing trainer profile
     * @param trainer the trainer entity to update
     * @return the updated trainer
     */
    Trainer updateTrainer(UpdateTrainer trainer);

    /**
     * Retrieves a trainer profile by its unique identifier
     * @param trainerId the ID of the trainer
     * @return the found trainer
     */
    Trainer getTrainer(long trainerId);


    /**
     * Checks if {@link Trainer} does exist with provided id
     * @param id the unique trainee id
     * @return true if exists, false otherwise
     * */
    boolean existsById(long id);

    Trainer getTrainerByUsername(String username);

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);
}