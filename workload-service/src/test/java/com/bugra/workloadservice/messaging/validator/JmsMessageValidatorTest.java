package com.bugra.workloadservice.messaging.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JmsMessageValidatorTest {

    private JmsMessageValidator messageValidator;

    @Mock
    private Validator validator;

    @BeforeEach
    void setup(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
         this.validator = validatorFactory.getValidator();

        messageValidator = new JmsMessageValidator(validator);
    }

    record DummyDto(
            @NotBlank(message = "Username cannot be blank")
            String username
    ) { }

    @Test
    @DisplayName("Should return empty set when data is valid")
    void validate_shouldReturnEmptySet() {
        DummyDto dto = new DummyDto("john.doe");

        Set<ConstraintViolation<DummyDto>>  violations = messageValidator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should return violations when data is invalid")
    void validate_shouldReturnViolations() {
        DummyDto dto = new DummyDto(null);

        Set<ConstraintViolation<DummyDto>>  violations = messageValidator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should return reason")
    void getReason_shouldReturnReason() {
        String testReason = "[username] - Username cannot be blank";
        DummyDto dto = new DummyDto(null);

        Set<ConstraintViolation<DummyDto>>  violations = messageValidator.validate(dto);
        String actualReason = messageValidator.getReason(violations);


        assertEquals(testReason, actualReason);

    }
}