package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

public record UpdateTrainer(
        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String username,

        @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.FIRST_NAME_SIZE)
        String firstName,

        @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.LAST_NAME_SIZE)
        String lastName,

        @NotBlank(message = ValidationMessages.SPECIALIZATION_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.SPECIALIZATION_SIZE)
        String specialization,

        @NotNull(message = ValidationMessages.STATUS_REQUIRED)
        Boolean isActive
) {
}
