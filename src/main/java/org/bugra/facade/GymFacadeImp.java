package org.bugra.facade;

import org.bugra.dto.*;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacadeImp implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public GymFacadeImp(TraineeService traineeService,
                        TrainerService trainerService,
                        TrainingService trainingService,
                        AuthService authService, UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authService = authService;
        this.userService = userService;
    }

    // Auth Methods
    @Override
    public UserResponse login(LoginUser loginUser) {
        return authService.login(loginUser);
    }

    @Override
    public boolean changePassword(ChangePassword changePassword) {
        return authService.changePassword(changePassword);
    }

    // Trainee Methods
    @Override
    public Trainee createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    @Override
    public Trainee getTrainee(long id) {
        return traineeService.getTrainee(id);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    @Override
    public void toggleTraineeActive(String username) {
        userService.toggleActiveStatus(username);
    }

    @Override
    public List<Training> getTraineeTrainings(TraineeTrainingFilter filter) {
        return trainingService.getTraineeTrainings(filter);
    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Override
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);
    }

    // Trainer Methods
    @Override
    public Trainer createTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }

    @Override
    public Trainer getTrainer(long id) {
        return trainerService.getTrainer(id);
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }

    @Override
    public void toggleTrainerActive(String username) {
        userService.toggleActiveStatus(username);
    }

    public List<Training> getTrainerTrainings(TrainerTrainingFilter filter) {
        return trainingService.getTrainerTrainings(filter);
    }

    @Override
    public Training createTraining(CreateTraining createTraining) {
        return trainingService.createTraining(createTraining);
    }

    @Override
    public Training getTraining(long id) {
        return trainingService.getTraining(id);
    }
}