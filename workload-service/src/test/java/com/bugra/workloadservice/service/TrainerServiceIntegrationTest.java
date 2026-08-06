package com.bugra.workloadservice.service;

import com.bugra.workloadservice.dto.TrainerDto;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.model.YearlyWorkload;
import com.bugra.workloadservice.repo.TrainerRepo;
import com.bugra.workloadservice.service.impl.TrainerServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerServiceIntegrationTest {

    @Autowired
    private TrainerServiceImp trainerService;

    @Autowired
    private TrainerRepo trainerRepo;

    @Test
    @DisplayName("Should save trainer with yearly and monthly work loads")
    void saveTrainerRecord_ShouldSaveTrainer() {
        LocalDateTime testDate = LocalDateTime.of(2026, 8, 15, 10, 0);

        TrainerDto dto = new TrainerDto(
                "Bobby",
                "Brown",
                "bobby.brown",
                true,
                testDate,
                10,
                ActionType.ADD
        );

        trainerService.saveTrainerRecord(dto);

        Trainer savedTrainer = trainerRepo.findByUsername("bobby.brown").orElseThrow();

        assertEquals("Bobby", savedTrainer.getFirstName());
        assertEquals("Brown", savedTrainer.getLastName());
        assertTrue(savedTrainer.isActive());

        assertNotNull(savedTrainer.getWorkloads());
        assertEquals(1, savedTrainer.getWorkloads().size());
        assertTrue(savedTrainer.getWorkloads().containsKey("2026"));

        YearlyWorkload savedYearly = savedTrainer.getWorkloads().get("2026");
        assertEquals("2026", savedYearly.getYear());
        assertNotNull(savedYearly.getTrainer());

        assertNotNull(savedYearly.getMonths());
        assertEquals(1, savedYearly.getMonths().size());
        assertTrue(savedYearly.getMonths().containsKey("AUGUST"));

        int savedDuration = savedYearly.getMonths().get("AUGUST").getDuration();
        assertEquals(10, savedDuration);
    }
}