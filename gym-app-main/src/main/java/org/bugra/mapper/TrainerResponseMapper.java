package org.bugra.mapper;


import org.bugra.dto.response.TrainerProfileResponse;
import org.bugra.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerResponseMapper {

    public TrainerProfileResponse mapToTrainerProfileResponse(Trainer trainer){
        return TrainerProfileResponse.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().isActive())
                .trainees(trainer.getTrainees().stream()
                        .map(trainee -> new TrainerProfileResponse.TraineeListSummary(
                                trainee.getUser().getUsername(),
                                trainee.getUser().getFirstName(),
                                trainee.getUser().getLastName()
                        ))
                        .toList())
                .build();
    }
}
