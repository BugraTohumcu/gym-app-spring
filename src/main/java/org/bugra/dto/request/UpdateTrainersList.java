package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

import java.util.List;

public record UpdateTrainersList(
        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String username,

        @Size(message = ValidationMessages.TRAINER_LIST_SIZE)
        List<String> trainerUsernames
) {
}
