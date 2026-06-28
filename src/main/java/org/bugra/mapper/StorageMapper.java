package org.bugra.mapper;

import org.bugra.model.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

/**
 * <p>This class responsible for paring from csv to object such as
 * {@link Trainee} {@link Trainer} {@link Training} and also format object to csv </p>
 * */
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

        Training training = new Training();

        training.setId(Long.parseLong(parts[0]));
        training.setTraineeId(Long.parseLong(parts[1]));
        training.setTrainerId(Long.parseLong(parts[2]));
        training.setTrainingName(parts[3]);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(parts[4]);
        training.setTrainingType(trainingType);

        training.setTrainingDate(LocalDate.parse(parts[5]));
        training.setTrainingDuration(Integer.parseInt(parts[6]));

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

    public String formatTrainer(Trainer t) {
        return String.join(",",
                String.valueOf(t.getId()),
                t.getFirstName(),
                t.getLastName(),
                t.getUsername(),
                t.getPassword(),
                String.valueOf(t.isActive()),
                t.getSpecialization()
        );
    }

    public String formatTrainee(Trainee t) {
        return String.join(",",
                String.valueOf(t.getId()),
                t.getFirstName(),
                t.getLastName(),
                t.getUsername(),
                t.getPassword(),
                String.valueOf(t.isActive()),
                t.getDateOfBirth().toString(),
                t.getAddress()
        );
    }

    public String formatTraining(Training t) {
        return String.join(",",
                String.valueOf(t.getId()),
                String.valueOf(t.getTraineeId()),
                String.valueOf(t.getTrainerId()),
                t.getTrainingName(),
                t.getTrainingType().getTrainingTypeName(),
                t.getTrainingDate().toString(),
                String.valueOf(t.getTrainingDuration())
        );
    }


    // Remove spaces from before and after of parts
    private String[] trimParts(String[] part){
        for (int i = 0; i < part.length; i++) {
            part[i] = part[i].trim();
        }
        return part;
    }
}