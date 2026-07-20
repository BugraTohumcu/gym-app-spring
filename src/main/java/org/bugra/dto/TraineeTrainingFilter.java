package org.bugra.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

import java.time.LocalDate;

public record TraineeTrainingFilter(

        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String traineeUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        String trainingType
) {
    @AssertTrue(message = ValidationMessages.DATE_INTERVAL)
    public boolean isDateRangeValid() {
        return fromDate == null || toDate == null || !fromDate.isAfter(toDate);
    }
}