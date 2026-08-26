package com.bugra.workloadservice.service;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.repo.TrainerRepo;
import com.bugra.workloadservice.service.impl.TrainerServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepo trainerRepo;

    @InjectMocks
    private TrainerServiceImp trainerService;

    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    @Test
    @DisplayName("Should save a new trainer with yearly and monthly workloads")
    void saveTrainerRecord_ShouldSaveNewTrainer() {
        LocalDate testDate = LocalDate.now().plusDays(1);
        String year = String.valueOf(testDate.getYear());
        Month month = testDate.getMonth();

        TrainerDto dto = new TrainerDto(
                "Bobby", "Brown", "bobby.brown", true, testDate, 10, ActionType.ADD
        );

        when(trainerRepo.findByUsername("bobby.brown")).thenReturn(Optional.empty());

        trainerService.saveTrainerRecord(dto);

        verify(trainerRepo).save(trainerCaptor.capture());
        Trainer savedTrainer = trainerCaptor.getValue();

        assertEquals("Bobby", savedTrainer.getFirstName());
        assertEquals("Brown", savedTrainer.getLastName());
        assertTrue(savedTrainer.isActive());

        assertNotNull(savedTrainer.getWorkloads());
        assertEquals(1, savedTrainer.getWorkloads().size());
        assertTrue(savedTrainer.getWorkloads().containsKey(year));
        assertEquals(10, savedTrainer.getWorkloads().get(year).get(month.name()));
    }

    @Test
    @DisplayName("Should subtract duration and prevent negative values")
    void saveTrainerRecord_shouldSubtractAndPreventNegative() {
        LocalDate testDate = LocalDate.now().plusDays(1);
        String year = String.valueOf(testDate.getYear());
        Month month = testDate.getMonth();

        Trainer existingTrainer = new Trainer();
        existingTrainer.setUsername("bobby.brown");

        Map<String, Map<String, Integer>> existingWorkloads = new HashMap<>();
        Map<String, Integer> monthlyWorkload = new HashMap<>();
        monthlyWorkload.put(month.name(), 10);
        existingWorkloads.put(year, monthlyWorkload);
        existingTrainer.setWorkloads(existingWorkloads);

        when(trainerRepo.findByUsername("bobby.brown")).thenReturn(Optional.of(existingTrainer));

        TrainerDto dto2 = new TrainerDto(
                "Bobby", "Brown", "bobby.brown", true, testDate, 20, ActionType.DELETE
        );

        trainerService.saveTrainerRecord(dto2);

        verify(trainerRepo).save(trainerCaptor.capture());
        Trainer savedTrainer = trainerCaptor.getValue();

        assertEquals(0, savedTrainer.getWorkloads().get(year).get(month.name()));
    }

    @Test
    @DisplayName("Should return workload summary for existing trainer")
    void getTrainerWorkload_shouldReturnWorkload() {
        Trainer mockTrainer = getMockTrainer();

        when(trainerRepo.findByUsername("bobby.brown")).thenReturn(Optional.of(mockTrainer));

        TrainerWorkloadResponse response = trainerService.getTrainerWorkload("bobby.brown");

        assertNotNull(response);
        assertEquals("bobby.brown", response.username());
        assertEquals("Bobby", response.firstName());

        assertEquals(10, response.workloads().get("2026").get("AUGUST"));
        assertEquals(20, response.workloads().get("2026").get("SEPTEMBER"));
    }

    private static Trainer getMockTrainer() {
        Trainer mockTrainer = new Trainer();
        mockTrainer.setUsername("bobby.brown");
        mockTrainer.setFirstName("Bobby");
        mockTrainer.setLastName("Brown");
        mockTrainer.setActive(true);

        Map<String, Map<String, Integer>> workloads = new HashMap<>();
        Map<String, Integer> workloads2026 = new HashMap<>();
        workloads2026.put(Month.AUGUST.name(), 10);
        workloads2026.put(Month.SEPTEMBER.name(), 20);
        workloads.put("2026", workloads2026);

        mockTrainer.setWorkloads(workloads);
        return mockTrainer;
    }
}