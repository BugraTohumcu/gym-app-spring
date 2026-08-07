package com.bugra.workloadservice.dto.request;

import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.shared.TrainerMessages;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TrainerDto(

        @NotBlank(message = TrainerMessages.FIRST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = TrainerMessages.FIRST_NAME_SIZE)
        String firstName,

        @NotBlank(message = TrainerMessages.LAST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = TrainerMessages.LAST_NAME_SIZE)
        String lastName,

        @NotBlank(message = TrainerMessages.USERNAME_REQUIRED)
        @Size(min = 2, max = 50, message = TrainerMessages.USERNAME_SIZE)
        String username,

        boolean isActive,

        @NotNull(message = TrainerMessages.TRAINING_DATE_REQUIRED)
        LocalDateTime trainingDate,

        @NotNull(message = TrainerMessages.TRAINING_DURATION)
        int duration,

        @NotNull    (message = TrainerMessages.ACTION_TYPE_REQUIRED)
        ActionType actionType

){

    @AssertTrue(message = TrainerMessages.TRAINING_DATE_FUTURE)
    public boolean isFuture(){
        return trainingDate.isAfter(LocalDateTime.now());
    }


    @AssertTrue(message = TrainerMessages.TRAINING_DURATION_VALUE)
    public boolean isGreaterThanZero(){
        return duration > 0;
    }
}
