package org.bugra.dto.client;

import lombok.Builder;
import org.bugra.enums.ActionType;

import java.time.LocalDate;

@Builder
public record SaveTrainerWorkload(
        String firstName,
        String lastName,
        String username,
        LocalDate trainingDate,
        ActionType actionType,
        boolean isActive,
        int duration
) {

}
