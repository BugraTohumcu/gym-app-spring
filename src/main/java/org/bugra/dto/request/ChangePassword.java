package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

public record ChangePassword(


        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
        @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
        String currentPassword,

        @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
        @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
        String newPassword
) {
}
