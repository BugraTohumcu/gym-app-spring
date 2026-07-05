package org.bugra.service;

import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.model.Training;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * Retrieves a filtered list of training sessions associated with a specific trainer.
     * @param filter the {@link TrainerTrainingFilter} containing the mandatory trainer identity and optional search criteria
     * @return a {@link List} of {@link Training} entities matching the specified criteria,
     * or an empty list if no records are found
     * @throws IllegalArgumentException if the provided filter object is null
     */
    List<Training> getTrainerTrainings(TrainerTrainingFilter filter);

    /**
     * Retrieves a filtered list of training sessions associated with a specific trainee.
     * @param filter the {@link TrainerTrainingFilter} containing the mandatory trainer identity and optional search criteria
     * @return a {@link List} of {@link Training} entities matching the specified criteria,
     * or an empty list if no records are found
     * @throws IllegalArgumentException if the provided filter object is null
     */
    List<Training> getTraineeTrainings(TraineeTrainingFilter filter);
}