package org.bugra.exception;

import org.bugra.dto.response.ErrorResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should return 422 status and proper error message when validation error occurs")
    void handleValidation_shouldReturn422() {
        FieldError firstNameError = new FieldError("registerTrainee",
                "firstName",
                "must not be blank");

        FieldError lastNameError = new FieldError("registerTrainee",
                "lastName",
                "must not be blank");

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(firstNameError, lastNameError));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(validationException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("must not be blank, must not be blank", response.getBody().message());
    }

    @Test
    @DisplayName("Should include transaction id in error response")
    void handleValidation_shouldIncludeTransactionIdFromMdc() {
        MDC.put("transactionId", "test-tx-123");
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = handler.handleValidation(validationException);

        assertEquals("test-tx-123", response.getBody().transactionId());
    }

    @Test
    @DisplayName("Should have null transaction id in response when MDC is empty")
    void handleValidation_shouldHaveNullTransactionIdWhenMdcEmpty() {
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = handler.handleValidation(validationException);

        assertNull(response.getBody().transactionId());
    }

    @Test
    @DisplayName("Should return 500 for generic errors")
    void handleGeneric_shouldReturn500WithGenericMessage() {
        Exception unexpected = new RuntimeException("something exploded");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(unexpected);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }

    @Test
    @DisplayName("Should not leak exception details")
    void handleGeneric_shouldNotLeakExceptionDetailsToClient() {
        Exception unexpected = new NullPointerException("sensitive internal detail");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(unexpected);

        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}