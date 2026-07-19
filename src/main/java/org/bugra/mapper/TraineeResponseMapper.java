package org.bugra.mapper;

import org.bugra.dto.response.TraineeProfileResponse;
import org.bugra.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeResponseMapper {


    public TraineeProfileResponse mapToTraineeProfileResponse(Trainee trainee){
        return TraineeProfileResponse.builder()
                .username(trainee.getUser().getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainee.getTrainers().stream()
                        .map(trainer -> new TraineeProfileResponse.TrainerSummary(
                                trainer.getUser().getUsername(),
                                trainer.getUser().getFirstName(),
                                trainer.getUser().getLastName(),
                                trainer.getSpecialization().getTrainingTypeName()
                        ))
                        .toList())
                .build();
    }
 }
