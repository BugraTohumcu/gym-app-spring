package com.bugra.workloadservice.service;

import com.bugra.workloadservice.dto.request.TrainerDto;
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
import java.time.Month;

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
        LocalDateTime testDate = LocalDateTime.now().plusDays(1);
        String year = String.valueOf(testDate.getYear());
        Month month = testDate.getMonth();

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
        assertTrue(savedTrainer.getWorkloads().containsKey(year));

        YearlyWorkload savedYearly = savedTrainer.getWorkloads().get(year);
        assertEquals(year, savedYearly.getYear());
        assertNotNull(savedYearly.getTrainer());

        assertNotNull(savedYearly.getMonths());
        assertEquals(1, savedYearly.getMonths().size());
        assertTrue(savedYearly.getMonths().containsKey(month.name()));

        int savedDuration = savedYearly.getMonths().get(month.name()).getDuration();
        assertEquals(10, savedDuration);
    }

    @Test
    @DisplayName("Should subtract duration and prevent negative")
    void saveTrainerRecord_shouldAddDuration(){
        LocalDateTime testDate = LocalDateTime.now().plusDays(1);
        String year = String.valueOf(testDate.getYear());
        Month month = testDate.getMonth();

        // save to db first
        TrainerDto dto1 = new TrainerDto(
                "Bobby",
                "Brown",
                "bobby.brown",
                true,
                testDate,
                10,
                ActionType.ADD
        );
        trainerService.saveTrainerRecord(dto1);

        // second request
        TrainerDto dto2 = new TrainerDto(
                "Bobby",
                "Brown",
                "bobby.brown",
                true,
                testDate,
                20,
                ActionType.DELETE
        );
        trainerService.saveTrainerRecord(dto2);

        Trainer savedTrainer = trainerRepo.findByUsername("bobby.brown").orElseThrow();

        assertEquals(dto2.username(), savedTrainer.getUsername());

        YearlyWorkload savedYearly = savedTrainer.getWorkloads().get(year);
        assertEquals(year, savedYearly.getYear());
        assertNotNull(savedYearly.getTrainer());

        assertTrue(savedYearly.getMonths().containsKey(month.name()));

        int savedDuration = savedYearly.getMonths().get(month.name()).getDuration();
        assertEquals(0, savedDuration);
    }
}