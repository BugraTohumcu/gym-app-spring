package org.bugra.dto;

import java.time.LocalDate;

public record TraineeTrainingFilter(
        String traineeUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        String trainingType
) {}