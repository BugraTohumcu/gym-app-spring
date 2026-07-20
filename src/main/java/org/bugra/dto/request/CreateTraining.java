package org.bugra.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

import java.time.LocalDate;

public record CreateTraining(
        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String trainerUsername,

        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String traineeUsername,

        @NotBlank(message = ValidationMessages.TRAINING_NAME_REQUIRED)
        @Size(min = 2, max = 20, message = ValidationMessages.TRAINING_NAME_SIZE)
        String trainingName,

        @NotBlank(message = ValidationMessages.TRAINING_TYPE_NAME_REQUIRED)
        @Size(min = 2, max = 20, message = ValidationMessages.TRAINING_TYPE_NAME_SIZE)
        String trainingTypeName,

        @Future(message = ValidationMessages.TRAINING_DATE_FUTURE)
        LocalDate trainingDate,

        @NotBlank(message = ValidationMessages.TRAINING_DURATION)
        int trainingDuration
) {
    @AssertTrue(message = ValidationMessages.TRAINING_DURATION_VALUE)
    public boolean isGreaterThanZero(){
        return trainingDuration > 0;
    }

}