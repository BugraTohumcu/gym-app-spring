package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.bugra.shared.ValidationMessages;

@Builder
public record RegisterTrainer(

        @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.FIRST_NAME_SIZE)
        String firstName,

        @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.LAST_NAME_SIZE)
        String lastName,

        @NotBlank(message = ValidationMessages.SPECIALIZATION_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.SPECIALIZATION_SIZE)
        String specialization
) {
}