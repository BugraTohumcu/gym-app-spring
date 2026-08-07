package org.bugra.dto.client;

import org.bugra.enums.ActionType;

import java.time.LocalDateTime;

public record SaveTrainerWorkload(
        String firstName,
        String lastName,
        String username,
        LocalDateTime trainingDate,
        ActionType actionType,
        boolean isActive,
        int duration
) {

}
