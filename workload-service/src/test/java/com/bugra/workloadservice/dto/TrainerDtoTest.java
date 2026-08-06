package com.bugra.workloadservice.dto;

import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.shared.TrainerMessages;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDtoTest {

    private Validator validator;

    @BeforeEach
    void setup(){
        try(ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()){
            validator = validatorFactory.getValidator();
        }
    }


    @Test
    @DisplayName("Should violate when training date is in the past")
    void isFuture_shouldViolateWhenPast() {
        TrainerDto dto = new TrainerDto(
          "John",
          "Doe",
          "joh.doe",
          true,
                LocalDateTime.now().minusDays(2),
          120,
          ActionType.ADD
        );

        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());

        boolean isThere = violations.stream()
                .anyMatch(v -> v.getMessage().equals(TrainerMessages.TRAINING_DATE_FUTURE));
        assertTrue(isThere);

    }

    @Test
    @DisplayName("Should violate when training duration is zero")
    void isGreaterThanZero_shouldViolateWhenZero() {
        TrainerDto dto = new TrainerDto(
                "John",
                "Doe",
                "joh.doe",
                true,
                LocalDateTime.now().plusDays(3),
                -1,
                ActionType.ADD
        );

        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());

        boolean isThere = violations.stream()
                .anyMatch(v -> v.getMessage().equals(TrainerMessages.TRAINING_DURATION_VALUE));
        assertTrue(isThere);
    }
}