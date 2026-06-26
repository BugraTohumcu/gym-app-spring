package org.bugra.mapper;

import org.bugra.model.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class StorageMapper {

    public Trainer parseTrainer(String line) {
        String[] parts = trimParts(line.split(","));

        Trainer trainer = new Trainer();
        parseUser(trainer, parts);
        trainer.setSpecialization(parts[6]);
        return trainer;
    }

    public Trainee parseTrainee(String line) {
        String[] parts = trimParts(line.split(","));

        Trainee trainee = new Trainee();
        parseUser(trainee, parts);
        trainee.setDateOfBirth(LocalDate.parse(parts[6]));
        trainee.setAddress(parts[7]);
        return trainee;
    }

    public Training parseTraining(String line) {
        String[] parts = trimParts(line.split(","));

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(parts[4]);

        Training training = new Training();
        training.setTraineeId(Long.parseLong(parts[0]));
        training.setTrainerId(Long.parseLong(parts[1]));
        training.setTrainingName(parts[2]);
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.parse(parts[3]));
        training.setTrainingDuration(Integer.parseInt(parts[5]));

        return training;
    }

    private void parseUser(User user, String[] parts) {
        user.setId(Long.parseLong(parts[0]));
        user.setFirstName(parts[1]);
        user.setLastName(parts[2]);
        user.setUsername(parts[3]);
        user.setPassword(parts[4]);
        user.setActive(Boolean.parseBoolean(parts[5]));
    }


    // Remove spaces from before and after of parts
    private String[] trimParts(String[] part){
        for (int i = 0; i < part.length; i++) {
            part[i] = part[i].trim();
        }
        return part;
    }
}