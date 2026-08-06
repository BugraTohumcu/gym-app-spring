package org.bugra.mapper;

import org.bugra.dto.response.TraineeTrainings;
import org.bugra.dto.response.TrainerTrainings;
import org.bugra.model.Training;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingResponseMapper {

    public List<TraineeTrainings> mapToTraineeTrainings(List<Training> trainings){
        return trainings.stream()
                .map(training -> TraineeTrainings.builder()
                        .trainingName(training.getTrainingName())
                        .trainingType(training.getTrainingType())
                        .date(training.getTrainingDate())
                        .duration(training.getTrainingDuration())
                        .trainerName(training.getTrainer().getUser().getUsername())
                        .build())
                .toList();
    }

    public List<TrainerTrainings> mapToTrainerTrainings(List<Training> trainings){
        return trainings.stream()
                .map(training -> TrainerTrainings.builder()
                        .trainingName(training.getTrainingName())
                        .trainingType(training.getTrainingType())
                        .date(training.getTrainingDate())
                        .duration(training.getTrainingDuration())
                        .traineeName(training.getTrainee().getUser().getUsername())
                        .build())
                .toList();
    }
}
