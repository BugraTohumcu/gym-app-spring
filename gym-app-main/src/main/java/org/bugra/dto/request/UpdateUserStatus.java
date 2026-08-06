package org.bugra.dto.request;

import jakarta.validation.constraints.NotNull;
import org.bugra.shared.ValidationMessages;

public record UpdateUserStatus(

        @NotNull(message = ValidationMessages.STATUS_REQUIRED)
        Boolean status
) {
}
