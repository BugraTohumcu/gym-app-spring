package org.bugra.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

import java.time.LocalDate;

public record TrainerTrainingFilter(

        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String trainerUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String traineeName
) {
    @AssertTrue(message = ValidationMessages.DATE_INTERVAL)
    public boolean isDateRangeValid() {
        return fromDate == null || toDate == null || !fromDate.isAfter(toDate);
    }
}