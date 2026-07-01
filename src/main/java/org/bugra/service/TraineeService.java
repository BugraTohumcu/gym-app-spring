package org.bugra.service;


import org.bugra.model.Trainee;
import org.bugra.exception.UserNotFoundException;


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
    Trainee createTrainee(Trainee trainee);
    /**
     * <p>Updates existing profile with given details</p>
     * @param trainee new trainee for update
     * @return the updated trainee
     * @throws UserNotFoundException If user does not exist
     * */
    Trainee updateTrainee(Trainee trainee);
    /**
     * <p>Deletes existing trainee with given trainee id</p>
     * @param traineeId the unique id for trainee to retrieve
     * @return success status of the deletion process
     * @throws UserNotFoundException If user does not exist
     * */
    boolean deleteTrainee(long traineeId);
    /**
     * <p>Retrieves trainee via given id</p>
     * @param traineeId the unique id for trainee to retrieve
     * @return Retrieved trainee
     * @throws UserNotFoundException If user does not exist
     * */
    Trainee getTrainee(long traineeId);

}