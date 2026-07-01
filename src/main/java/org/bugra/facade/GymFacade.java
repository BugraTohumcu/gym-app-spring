package org.bugra.facade;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;

public interface GymFacade {
    Trainee createTrainee(Trainee trainee);
    Trainee getTrainee(long id);
    Trainee updateTrainee(Trainee trainee);
    boolean deleteTrainee(long id);

    Trainer createTrainer(Trainer trainer);
    Trainer getTrainer(long id);
    Trainer updateTrainer(Trainer trainer);

    Training createTraining(Training training);
    Training getTraining(long id);
}