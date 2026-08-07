package com.bugra.workloadservice.controller;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;



    @Test
    @DisplayName("Post: /trainer - Should return 200")
    void createTrainerRecord_shouldCreateTrainer() throws Exception {
        TrainerDto dto = new TrainerDto(
                "John",
                "Doe",
                "joh.doe",
                true,
                LocalDate.now().plusDays(2),
                120,
                ActionType.ADD
        );

        doNothing().when(trainerService).saveTrainerRecord(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/workload")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Post: /trainer - Should return 400 when validation failed")
    void createTrainerRecord_shouldReturn400() throws Exception {
        TrainerDto dto = new TrainerDto(
                "John",
                "Doe",
                "joh.doe",
                true,
                LocalDate.now().minusDays(2),
                120,
                ActionType.ADD
        );

        doNothing().when(trainerService).saveTrainerRecord(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/workload")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest
    @MethodSource("provideDtoForIsBlank")
    public void createTrainerRecord_shouldReturnFail(String firstName, String lastName, String username) throws Exception {

        TrainerDto trainerDto = new TrainerDto(
                firstName,
                lastName,
                username,
                true,
                LocalDate.now().plusDays(2),
                120,
                ActionType.ADD
        );
        doNothing().when(trainerService).saveTrainerRecord(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/workload")
                        .content(objectMapper.writeValueAsString(trainerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }


    private static Stream<Arguments> provideDtoForIsBlank(){
        return Stream.of(
                // Empty username
                Arguments.of("John", "Doe", ""),

                // Empty first name
                Arguments.of("", "Doe", "john.doe"),

                // Empty last name
                Arguments.of("John", "", "john.doe")
        );
    }

    @Test
    @DisplayName("GET: /Should return 200 OK and trainer workload when username is valid")
    void getWorkload_ShouldReturnWorkloadResponse() throws Exception {
        String username = "bobby.brown";
        TrainerWorkloadResponse mockResponse = new TrainerWorkloadResponse(
                username,
                "Bobby",
                "Brown",
                true,
                Map.of("2026", Map.of("AUGUST", 10))
        );

        when(trainerService.getTrainerWorkload(eq(username))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/workload")
                        .param("username",username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Bobby"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.workloads.['2026'].AUGUST").value(10));


    }
}