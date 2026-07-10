package org.bugra.util;

import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;

public final class TrainerValidator {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 50;

    private TrainerValidator() {
    }

    public static void validate(Trainer trainer) {
        ValidationUtils.requireNotNull(trainer, "Trainer");
        ValidationUtils.requireNotNull(trainer.getUser(), "User");
        ValidationUtils.requireNotNull(trainer.getSpecialization(), "Specialization");

        validateUser(trainer.getUser());
        validateSpecialization(trainer.getSpecialization());
    }

    private static void validateUser(User user) {
        ValidationUtils.requireLengthBetween(
                user.getFirstName(), NAME_MIN_LENGTH, NAME_MAX_LENGTH, "First name");
        ValidationUtils.requireLengthBetween(
                user.getLastName(), NAME_MIN_LENGTH, NAME_MAX_LENGTH, "Last name");
    }

    private static void validateSpecialization(TrainingType trainingType) {
        ValidationUtils.requireNonBlank(
                trainingType.getTrainingTypeName(), "Specialization (Training Type)");
    }
}