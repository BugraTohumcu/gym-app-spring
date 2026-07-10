package org.bugra.util;

import org.bugra.model.Trainee;
import org.bugra.model.User;

public final class TraineeValidator {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 50;
    private static final int ADDRESS_MAX_LENGTH = 255;

    private TraineeValidator() {
    }

    public static void validate(Trainee trainee) {
        ValidationUtils.requireNotNull(trainee, "Trainee");
        ValidationUtils.requireNotNull(trainee.getUser(), "User");

        validateUser(trainee.getUser());

        if (trainee.getAddress() != null) {
            ValidationUtils.requireMaxLength(trainee.getAddress(), ADDRESS_MAX_LENGTH, "Address");
        }

        if (trainee.getDateOfBirth() != null) {
            ValidationUtils.requirePastDate(trainee.getDateOfBirth(), "Birth date");
        }
    }

    private static void validateUser(User user) {
        ValidationUtils.requireLengthBetween(
                user.getFirstName(), NAME_MIN_LENGTH, NAME_MAX_LENGTH, "First name");
        ValidationUtils.requireLengthBetween(
                user.getLastName(), NAME_MIN_LENGTH, NAME_MAX_LENGTH, "Last name");
    }
}