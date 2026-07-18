package org.bugra.util;

import org.bugra.dto.request.CreateTraining;

import java.time.LocalDate;

public final class TrainingValidator {

    private TrainingValidator() {}

    public static void validate(CreateTraining createTraining) {
        ValidationUtils.requireNotNull(createTraining, "Training");
        ValidationUtils.requireNonBlank(createTraining.trainingName(), "Training name");
        ValidationUtils.requireNonBlank(createTraining.trainingTypeName(), "Training type");
        ValidationUtils.requireNonBlank(createTraining.trainerUsername(), "Trainer username");
        ValidationUtils.requireNotNull(createTraining.traineeId(), "Trainee id");
        validateDateAndDuration(createTraining.trainingDate(), createTraining.trainingDuration());
    }

    public static void validateDateAndDuration(LocalDate date, int duration) {
        ValidationUtils.requireNotNull(date, "Training date");
        ValidationUtils.requireTrue(duration > 0, "Training duration must be positive");
    }
}