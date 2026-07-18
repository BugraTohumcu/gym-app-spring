package org.bugra.service;

import org.bugra.dto.request.CreateTraining;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.model.Training;

import java.util.List;

import org.bugra.exception.TrainingNotFoundException;

/**
 * Service interface for creating and getting {@link Training} profiles.
 */
public interface TrainingService {

    /**
     * <p>Creates a new training profile</p>
     * @param createTraining the training request dto to create a training
     * @return the created training
     * @throws IllegalArgumentException if {@link Training} is null
     */
    Training createTraining(CreateTraining createTraining);

    /**
     * <p>Retrieves a training profile by its unique identifier</p>
     * @param trainingId the ID of the training
     * @throws TrainingNotFoundException if training does not exist
     * @return the found training
     */
    Training getTraining(long trainingId);

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