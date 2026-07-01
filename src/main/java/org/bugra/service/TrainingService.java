package org.bugra.service;

import org.bugra.model.Training;

import java.time.LocalDate;
import org.bugra.exception.TrainingNotFoundException;

/**
 * Service interface for creating and getting {@link Training} profiles.
 */
public interface TrainingService {

    /**
     * <p>Creates a new training profile</p>
     * @param training the training entity to create
     * @return the created training
     * @throws IllegalArgumentException if {@link Training} is null
     */
    Training createTraining(Training training);

    /**
     * <p>Retrieves a training profile by its unique identifier</p>
     * @param trainingId the ID of the training
     * @throws TrainingNotFoundException if training does not exists
     * @return the found training
     */
    Training getTraining(long trainingId);

    /**
     *<p>Validate the training date and training duration time</p>
     * @param date the provided training date
     * @param duration the provided training duration;
     * @throws IllegalArgumentException if provided date or duration invalid
    */
    void validateTrainingDateAndDuration(LocalDate date, int duration);
}