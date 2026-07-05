package org.bugra.facade;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.service.TraineeService;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacadeImp implements GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacadeImp(TraineeService traineeService,
                        TrainerService trainerService,
                        TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // Trainee Methods
    @Override
    public Trainee createTrainee(Trainee trainee) { return traineeService.createTrainee(trainee); }

    @Override
    public Trainee getTrainee(long id) { return traineeService.getTrainee(id); }

    @Override
    public Trainee updateTrainee(Trainee trainee) { return traineeService.updateTrainee(trainee); }

    @Override
    public boolean deleteTrainee(long id) { return traineeService.deleteTraineeById(id); }

    // Trainer Methods
    @Override
    public Trainer createTrainer(Trainer trainer) { return trainerService.createTrainer(trainer); }

    @Override
    public Trainer getTrainer(long id) { return trainerService.getTrainer(id); }

    @Override
    public Trainer updateTrainer(Trainer trainer) { return trainerService.updateTrainer(trainer); }

    // Training Methods
    @Override
    public Training createTraining(Training training) { return trainingService.createTraining(training); }

    @Override
    public Training getTraining(long id) { return trainingService.getTraining(id); }
}