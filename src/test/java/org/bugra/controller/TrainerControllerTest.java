package org.bugra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.response.TrainerProfileResponse;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.GlobalExceptionHandler;
import org.bugra.exception.UserNotFoundException;
import org.bugra.mapper.TrainerResponseMapper;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.bugra.service.TrainerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainerResponseMapper trainerResponseMapper;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setup(){
          this.mockMvc = MockMvcBuilders.standaloneSetup(trainerController)
                  .setControllerAdvice(new GlobalExceptionHandler())
                  .build();
    }

    @Test
    @DisplayName("POST /trainer/register - Success")
    void registerTrainee_shouldRegisterAndReturn200() throws Exception{
        RegisterTrainer registerTrainer = new RegisterTrainer(
                "John",
                "Doe",
                "Swimming"
        );
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        trainer.getUser().setUsername("john.doe");
        trainer.getUser().setPassword("123");

        when(trainerService.createTrainer(any())).thenReturn(trainer);

        mockMvc.perform(post("/trainer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerTrainer)))
                .andExpect(status().isOk());
    }

    static Stream<Arguments> invalidRegisterTrainerProvider() {
        return Stream.of(
                Arguments.of("First name blank", "", "Doe", "Swimming"),
                Arguments.of("Last name blank", "John", "", "Swimming"),
                Arguments.of("Specialization blank", "John", "Doe", "")
        );
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("POST /trainer/register - Fail: Validation")
    @MethodSource("invalidRegisterTrainerProvider")
    void registerTrainee_shouldReturn422WhenFieldIsBlank(
            String testName, String firstName, String lastName, String specialization) throws Exception {

        RegisterTrainer registerTrainer = new RegisterTrainer(firstName, lastName, specialization);

        mockMvc.perform(post("/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerTrainer)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /trainer/{username} - Success")
    void getTrainerProfile_shouldReturn200() throws Exception {

        Trainer trainer = new Trainer();
        TrainerProfileResponse response = TrainerProfileResponse.builder()
                .firstName("Jane")
                .lastName("Smith")
                .isActive(true)
                .trainees(java.util.List.of())
                .build();

        when(trainerService.getTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainerResponseMapper.mapToTrainerProfileResponse(trainer)).thenReturn(response);

        mockMvc.perform(get("/trainer/jane.smith"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /trainer/{username} - Fail: User not found")
    void getTrainerProfile_shouldReturn404AndThrow() throws Exception {

        when(trainerService.getTrainerByUsername(anyString())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/trainer/jane.smith"))
                .andExpect(status().isNotFound());
    }


}