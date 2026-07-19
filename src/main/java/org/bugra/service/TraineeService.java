package org.bugra.service;


import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.UpdateTrainee;
import org.bugra.dto.response.UserResponse;
import org.bugra.model.Trainee;
import org.bugra.exception.UserNotFoundException;

import java.util.List;


/**
 * Service interface responsible for managing {@link  Trainee} profiles
 <ul>
 * <li>Generating unique, serial-numbered credentials based on specific naming rules</li>
 * <li>Performing data enrichment during profile creation</li>
 * <li>Handling profile updates, retrievals, and deletions with proper error verification</li>
 * </ul>
 * */
public interface TraineeService {
    /**
     * <p>Creates new trainee profile, calculates unique username and random
     * password and persists the trainee data</p>
     * @param trainee New trainee to save with null username and password fields
     * @return Saved trainee
     * */
    Trainee createTrainee(RegisterTrainee trainee);
    /**
     * <p>Updates existing profile with given details</p>
     * @param trainee new trainee for update
     * @return the updated trainee
     * @throws UserNotFoundException If user does not exist
     * */
    Trainee updateTrainee(UpdateTrainee trainee);
    /**
     * <p>Deletes existing trainee with given trainee id</p>
     * @param traineeId the unique id for trainee to retrieve
     * @return success status of the deletion process
     * @throws UserNotFoundException If user does not exist
     * */
    boolean deleteTraineeById(long traineeId);

    /**
     * <p>Deletes existing trainee with the given username </p>
     * @param username the unique username
     * @return the result of {@link UserService#deleteByUsername(String)}
     * */
    boolean deleteTraineeByUsername(String username);
    /**
     * <p>Retrieves trainee via given id</p>
     * @param traineeId the unique id for trainee to retrieve
     * @return Retrieved trainee
     * @throws UserNotFoundException If user does not exist
     * */
    Trainee getTrainee(long traineeId);


    /**
     * Checks if {@link Trainee} does exist with provided id
     * @param id the unique trainee id
     * @return true if exists, false otherwise
     * */
    boolean existsById(long id);

    Trainee getTraineeByUsername(String username);

    /**
     * Updates and synchronizes the list of trainers assigned to a specific trainee.
     *
     * @param traineeUsername  the unique username of the target trainee; must not be null or blank
     * @param trainerUsernames a {@link List} of unique usernames
     * @throws UserNotFoundException    if the trainee or any of the trainers cannot be found in the
     * @throws IllegalArgumentException if the {@code traineeUsername} is null/blank, or if {@code trainerUsernames} is null
     */
    void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames);
}