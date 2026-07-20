package org.bugra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bugra.dto.request.TraineeTrainingFilter;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.UpdateTrainee;
import org.bugra.dto.request.UpdateTrainersList;
import org.bugra.dto.response.TraineeProfileResponse;
import org.bugra.dto.response.TraineeTrainings;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.GlobalExceptionHandler;
import org.bugra.exception.UserNotFoundException;
import org.bugra.mapper.TraineeResponseMapper;
import org.bugra.mapper.TrainingResponseMapper;
import org.bugra.model.*;
import org.bugra.service.TraineeService;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeResponseMapper traineeResponseMapper;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingResponseMapper trainingResponseMapper;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(traineeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    @DisplayName("POST /trainee/register - Success")
    void registerTrainee_shouldRegisterTrainee() throws Exception {
        RegisterTrainee request = new RegisterTrainee(
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "USA road");

        UserResponse userResponse = new UserResponse("john.doe", "123");

        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("123");
        trainee.setUser(user);

        when(traineeService.createTrainee(any())).thenReturn(trainee);

        mockMvc.perform(post("/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userResponse.username()))
                .andExpect(jsonPath("$.password").value(userResponse.password()));
    }

    @Test
    @DisplayName("POST /trainee/register - Fail")
    void registerTrainee_shouldThrowWhenValidationFails() throws Exception {
        RegisterTrainee request = new RegisterTrainee(
                "",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "USA road");

        mockMvc.perform(post("/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /trainee/{username} - Success")
    void getTraineeProfile_shouldReturn200() throws Exception {
        Trainee trainee = new Trainee();

        TraineeProfileResponse response = TraineeProfileResponse.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("USA")
                .isActive(true)
                .trainers(List.of())
                .build();

        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
        when(traineeResponseMapper.mapToTraineeProfileResponse(trainee)).thenReturn(response);

        mockMvc.perform(get("/trainee/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("USA"))
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    @DisplayName("GET /trainee/{username} - Fail")
    void getTraineeProfile_shouldReturn404() throws Exception {

        when(traineeService.getTraineeByUsername("john.doe")).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/trainee/john.doe"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PUT /trainee - Success")
    void updateTraineeProfile_shouldReturn200() throws Exception {
        UpdateTrainee request = new UpdateTrainee("john.doe",
                "John", "Doe",
                LocalDate.of(2000,1,1), "USA",
                true);

        Trainee updatedTrainee = new Trainee();

        TraineeProfileResponse response = TraineeProfileResponse.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("USA")
                .isActive(true)
                .trainers(List.of())
                .build();

        when(traineeService.updateTrainee(any())).thenReturn(updatedTrainee);
        when(traineeResponseMapper.mapToTraineeProfileResponse(updatedTrainee)).thenReturn(response);

        mockMvc.perform(put("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(request.address()));
    }

    @Test
    @DisplayName("DELETE /trainee/{username} - Success")
    void deleteTrainee_shouldDeleteProfile() throws Exception {
        when(traineeService.deleteTraineeByUsername(any())).thenReturn(true);

        mockMvc.perform(delete("/trainee/john.doe"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /trainee/{username} - Fail")
    void deleteTrainee_shouldThrowWhenUserNotExist() throws Exception {
        when(traineeService.deleteTraineeByUsername(any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(delete("/trainee/john.doe"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /trainee/not-assigned - Success with data")
    void getAvailableTrainers_shouldReturn200WithData() throws Exception {

        User user = new User();
        user.setUsername("jane.smith");
        user.setFirstName("Jane");
        user.setLastName("Smith");

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Swimming");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);

        when(trainerService.getTrainersNotAssignedToTrainee(anyString()))
                .thenReturn(List.of(trainer));

        mockMvc.perform(get("/trainee/not-assigned?").param("username", "john.doe"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /trainee/trainers - Success")
    void updateTrainers_shouldReturn200() throws Exception {

        UpdateTrainersList updateTrainersList = new UpdateTrainersList(
                "jane.smith",
                List.of("jef.deff")
        );

        User trainerUser = new User();
        trainerUser.setUsername("jef.deff");
        trainerUser.setFirstName("Jef");
        trainerUser.setLastName("Deff");

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Swimming");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(type);

        Trainee trainee = new Trainee();
        trainee.setTrainers(Set.of(trainer));

        TraineeProfileResponse.TrainerSummary summary =
                new TraineeProfileResponse.TrainerSummary("jef.deff", "Jef", "Deff", "Swimming");

        when(traineeService.updateTraineeTrainers(anyString(), anyList())).thenReturn(trainee);
        when(traineeResponseMapper.mapToTrainerSummary(anyList())).thenReturn(List.of(summary));

        mockMvc.perform(put("/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersList)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /trainee/trainers - Fail: Trainee not found")
    void updateTrainers_shouldReturn404WhenTraineeNotFound() throws Exception {

        UpdateTrainersList updateTrainersList = new UpdateTrainersList(
                "jane.smith",
                List.of("jef.deff")
        );

        when(traineeService.updateTraineeTrainers(anyString(), anyList()))
                .thenThrow(new UserNotFoundException("Trainee not found"));

        mockMvc.perform(put("/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersList)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /trainee/trainers - Fail: One or more trainers not found")
    void updateTrainers_shouldReturn404WhenTrainerNotFound() throws Exception {

        UpdateTrainersList updateTrainersList = new UpdateTrainersList(
                "jane.smith",
                List.of("non.existent")
        );

        when(traineeService.updateTraineeTrainers(anyString(), anyList()))
                .thenThrow(new UserNotFoundException("Following trainers not found: [non.existent]"));

        mockMvc.perform(put("/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersList)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /trainee/trainers - Fail: Username blank")
    void updateTrainers_shouldReturn422WhenUsernameBlank() throws Exception {

        UpdateTrainersList updateTrainersList = new UpdateTrainersList(
                "",
                List.of("jef.deff")
        );

        mockMvc.perform(put("/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersList)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PUT /trainee/trainers - Fail: Trainer list empty")
    void updateTrainers_shouldReturn422WhenTrainerListEmpty() throws Exception {

        UpdateTrainersList updateTrainersList = new UpdateTrainersList(
                "jane.smith",
                List.of()
        );

        mockMvc.perform(put("/trainee/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainersList)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /trainee/trainings - Should return trainings list with 200 OK")
    void getTraineeTrainings_shouldReturnTrainingsList() throws Exception {
        List<Training> mockTrainings = List.of(new Training());
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("STRENGTH");

        TraineeTrainings responseDto = TraineeTrainings.builder()
                .trainingName("Leg Day")
                .trainingType(type)
                .date(LocalDate.of(2026, 7, 20))
                .duration(75)
                .trainerName("ronnie.fit")
                .build();

        when(trainingService.getTraineeTrainings(any(TraineeTrainingFilter.class)))
                .thenReturn(mockTrainings);
        when(trainingResponseMapper.mapToTraineeTrainings(mockTrainings))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/trainee/trainings")
                        .param("traineeUsername", "john.doe")
                        .param("trainingType", "STRENGTH")
                        .param("fromDate", "2026-07-01")
                        .param("toDate", "2026-07-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Leg Day"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("STRENGTH"))
                .andExpect(jsonPath("$[0].duration").value(75))
                .andExpect(jsonPath("$[0].trainerName").value("ronnie.fit"));

        verify(trainingService).getTraineeTrainings(any(TraineeTrainingFilter.class));
        verify(trainingResponseMapper).mapToTraineeTrainings(mockTrainings);
    }

    @Test
    @DisplayName("GET /trainee/trainings - Should return 422 when invalid date interval provided")
    void getTraineeTrainings_shouldReturn422WhenInvalidTimeProvided() throws Exception {

        mockMvc.perform(get("/trainee/trainings")
                        .param("traineeUsername", "john.doe")
                        .param("fromDate", "2027-07-01")
                        .param("toDate", "2026-07-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

}