package org.bugra.controller;

import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.response.UserResponse;
import org.bugra.service.TraineeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    TraineeService traineeService;

    @InjectMocks
    TraineeController traineeController;

    private RegisterTrainee createValidRegisterTrainee(){
        return new RegisterTrainee(
                "John",
                "Doe",
                LocalDate.of(2004, 7,7),
                "USA"
        );
    }

    @Test
    @DisplayName("Should return user response with 200 status code")
    void registerTrainee_shouldReturnUserAnd200() {
        RegisterTrainee trainee = createValidRegisterTrainee();

        when(traineeService.createTrainee((any()))).thenReturn(
                new UserResponse("john.doe", "123")
        );

        ResponseEntity<UserResponse> response = traineeController.registerTrainee(trainee);

        String expectedUsername = (trainee.firstName() + "." + trainee.lastName()).toLowerCase();

        assertNotNull(response.getBody());
        assertEquals(expectedUsername, response.getBody().username());
        assertEquals("123", response.getBody().password());
    }

    @Test
    @DisplayName("Should throw NullPointerException when request is null")
    void  test(){
        assertThrows(NullPointerException.class,
                () -> traineeController.registerTrainee(null));

        verifyNoInteractions(traineeService);
    }

}