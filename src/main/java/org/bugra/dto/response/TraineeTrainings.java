package org.bugra.dto.response;

import lombok.Builder;
import org.bugra.model.TrainingType;

import java.time.LocalDateTime;

@Builder
public record TraineeTrainings(
        String trainingName,
        LocalDateTime date,
        TrainingType trainingType,
        int duration,
        String trainerName
) {
}
