package org.bugra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.bugra.shared.ValidationMessages;

public record LoginUser(

        @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
        @Size(min = 3, max = 50, message = ValidationMessages.USERNAME_SIZE)
        String username,

        @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
        @Size(min = 2, max = 20, message = ValidationMessages.PASSWORD_SIZE)
        String password) { }