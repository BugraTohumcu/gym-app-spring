package org.bugra.dto.response;

import lombok.Builder;
import org.bugra.model.TrainingType;

import java.time.LocalDate;

@Builder
public record TraineeTrainings(
        String trainingName,
        LocalDate date,
        TrainingType trainingType,
        int duration,
        String trainerName
) {
}
