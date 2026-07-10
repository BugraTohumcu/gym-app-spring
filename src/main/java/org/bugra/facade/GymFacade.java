package org.bugra.facade;

import org.bugra.dto.*;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;

import java.util.List;

public interface GymFacade {

    UserResponse login(LoginUser loginUser);

    boolean changePassword(ChangePassword changePassword);

    Trainee createTrainee(Trainee trainee);

    Trainee getTrainee(long id);

    Trainee getTraineeByUsername(String username);

    Trainee updateTrainee(Trainee trainee);

    void deleteTraineeByUsername(String username);

    void toggleTraineeActive(String username);

    List<Training> getTraineeTrainings(TraineeTrainingFilter filter);

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);

    void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames);

    Trainer createTrainer(Trainer trainer);

    Trainer getTrainer(long id);

    Trainer getTrainerByUsername(String username);

    Trainer updateTrainer(Trainer trainer);

    void toggleTrainerActive(String username);

    List<Training> getTrainerTrainings(TrainerTrainingFilter filter);

    Training createTraining(CreateTraining createTraining);

    Training getTraining(long id);
}