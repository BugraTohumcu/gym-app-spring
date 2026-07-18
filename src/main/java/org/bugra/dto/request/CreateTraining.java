package org.bugra.dto.request;

import java.time.LocalDate;

public record CreateTraining(
        String trainerUsername,
        long traineeId,
        String trainingName,
        String trainingTypeName,
        LocalDate trainingDate,
        int trainingDuration
) {}