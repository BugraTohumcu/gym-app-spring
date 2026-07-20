package org.bugra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bugra.dto.request.CreateTraining;
import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.request.TrainerTrainingFilter;
import org.bugra.dto.request.UpdateTrainer;
import org.bugra.dto.response.TrainerProfileResponse;
import org.bugra.dto.response.TrainerTrainings;
import org.bugra.exception.GlobalExceptionHandler;
import org.bugra.exception.TrainingTypeNotFoundException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.mapper.TrainerResponseMapper;
import org.bugra.mapper.TrainingResponseMapper;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingResponseMapper trainingResponseMapper;

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

    @Test
    @DisplayName("PUT /trainer - Success")
    void updateTrainerProfile_shouldReturn200() throws Exception {

        UpdateTrainer updateTrainer = new UpdateTrainer(
                "jane.smith",
                "Jane",
                "Smith",
                "Swimming",
                true
        );

        Trainer trainer = new Trainer();
        TrainerProfileResponse response = TrainerProfileResponse.builder()
                .firstName("Jane")
                .lastName("Smith")
                .isActive(true)
                .trainees(java.util.List.of())
                .build();

        when(trainerService.updateTrainer(any())).thenReturn(trainer);
        when(trainerResponseMapper.mapToTrainerProfileResponse(trainer)).thenReturn(response);

        mockMvc.perform(put("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainer)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /trainer - Fail: User not found")
    void updateTrainerProfile_shouldReturn404AndThrow() throws Exception {

        UpdateTrainer updateTrainer = new UpdateTrainer(
                "jane.smith",
                "Jane",
                "Smith",
                "Swimming",
                true
        );

        when(trainerService.updateTrainer(any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainer)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /trainer/trainings - Should return trainings list with 200 OK")
    void getTrainerTrainings_shouldReturnTrainingsList() throws Exception {
        List<Training> mockTrainings = List.of(new Training());
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("STRENGTH");

        TrainerTrainings responseDto = TrainerTrainings.builder()
                .trainingName("Leg Day")
                .trainingType(type)
                .date(LocalDate.of(2026, 7, 20))
                .duration(75)
                .traineeName("jane.smith")
                .build();

        when(trainingService.getTrainerTrainings(any(TrainerTrainingFilter.class)))
                .thenReturn(mockTrainings);
        when(trainingResponseMapper.mapToTrainerTrainings(mockTrainings))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/trainer/trainings")
                        .param("trainerUsername", "ronnie.fit")
                        .param("traineeName", "jane.smith")
                        .param("fromDate", "2026-07-01")
                        .param("toDate", "2026-07-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Leg Day"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("STRENGTH"))
                .andExpect(jsonPath("$[0].duration").value(75))
                .andExpect(jsonPath("$[0].traineeName").value("jane.smith"));

        verify(trainingService).getTrainerTrainings(any(TrainerTrainingFilter.class));
        verify(trainingResponseMapper).mapToTrainerTrainings(mockTrainings);
    }

    @Test
    @DisplayName("GET /trainer/trainings - Should return 422 when invalid date interval provided")
    void getTrainerTrainings_shouldReturn422WhenInvalidTimeProvided() throws Exception {

        mockMvc.perform(get("/trainer/trainings")
                        .param("trainerUsername", "ronnie.fit")
                        .param("fromDate", "2027-07-01")
                        .param("toDate", "2026-07-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /trainer/trainings/training - Success")
    void createTraining_shouldReturn200() throws Exception {

        CreateTraining createTraining = new CreateTraining(
                "ronnie.fit",
                "jane.smith",
                "Leg Day",
                "STRENGTH",
                LocalDate.now().plusDays(7),
                60
        );

        mockMvc.perform(post("/trainer/trainings/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTraining)))
                .andExpect(status().isOk());

        verify(trainingService).createTraining(any(CreateTraining.class));
    }

    @Test
    @DisplayName("POST /trainer/trainings/training - Fail: Trainee or trainer not found")
    void createTraining_shouldReturn404WhenUserNotFound() throws Exception {

        CreateTraining createTraining = new CreateTraining(
                "ronnie.fit",
                "jane.smith",
                "Leg Day",
                "STRENGTH",
                LocalDate.now().plusDays(7),
                60
        );

        doThrow(new UserNotFoundException())
                .when(trainingService).createTraining(any(CreateTraining.class));

        mockMvc.perform(post("/trainer/trainings/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTraining)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /trainer/trainings/training - Fail: Training type not found")
    void createTraining_shouldReturn404WhenTrainingTypeNotFound() throws Exception {

        CreateTraining createTraining = new CreateTraining(
                "ronnie.fit",
                "jane.smith",
                "Leg Day",
                "NON_EXISTENT_TYPE",
                LocalDate.now().plusDays(7),
                60
        );

        doThrow(new TrainingTypeNotFoundException())
                .when(trainingService).createTraining(any(CreateTraining.class));

        mockMvc.perform(post("/trainer/trainings/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTraining)))
                .andExpect(status().isBadRequest());
    }

}