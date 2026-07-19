package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;
import java.time.LocalDate;

public record UpdateTrainee(
        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.FIRST_NAME_SIZE)
        String firstName,

        @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.LAST_NAME_SIZE)
        String lastName,

        @Past(message = ValidationMessages.DOB_PAST)
        LocalDate dateOfBirth,

        @Size(max = 100, message = ValidationMessages.ADDRESS_SIZE)
        String address,

        @NotNull(message = "Is Active status is required")
        Boolean isActive
) {}