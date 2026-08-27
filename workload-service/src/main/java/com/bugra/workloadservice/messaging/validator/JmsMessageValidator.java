package com.bugra.workloadservice.messaging.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JmsMessageValidator {

    private final Validator validator;

    public <T>Set<ConstraintViolation<T>> validate(T dto){
        return validator.validate(dto);
    }

    public <T>String getReason(Set<ConstraintViolation<T>> violations){
        return violations.stream()
                .map(e -> String.format("[%s] - %s", e.getPropertyPath(), e.getMessage()))
                .collect(Collectors.joining("-"));
    }
}
