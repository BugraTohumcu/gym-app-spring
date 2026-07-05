package org.bugra.dto;

import java.time.LocalDate;

public record TrainerTrainingFilter(
        String trainerUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String traineeName
) {}