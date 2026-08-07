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

import java.time.LocalDate;
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
        LocalDate testDate = LocalDate.now().plusDays(1);
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
        LocalDate testDate = LocalDate.now().plusDays(1);
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


    @Test
    @DisplayName("Should return all yearly and monthly workloads")
    void getTrainerWorkload_shouldReturnWorkload(){
        LocalDate testDate1 = LocalDate.now().plusDays(1);
        LocalDate testDate2 = LocalDate.now().plusMonths(1);

        int duration1 = 10;
        int duration2 = 20;

        String year1 = String.valueOf(testDate1.getYear());
        String year2 = String.valueOf(testDate2.getYear());

        Month month1 = testDate1.getMonth();
        Month month2 = testDate2.getMonth();

        // save to db first
        TrainerDto dto1 = new TrainerDto(
                "Bobby",
                "Brown",
                "bobby.brown",
                true,
                testDate1,
                duration1,
                ActionType.ADD
        );

        // second request
        TrainerDto dto2 = new TrainerDto(
                "Bobby",
                "Brown",
                "bobby.brown",
                true,
                testDate2,
                duration2,
                ActionType.ADD
        );

        trainerService.saveTrainerRecord(dto1);
        trainerService.saveTrainerRecord(dto2);


        var response = trainerService.getTrainerWorkload("bobby.brown");

        assertNotNull(response);

        assertEquals(
                duration1,
                response.workloads().get(year1).get(month1.name()));

        assertEquals(
                duration2,
                response.workloads().get(year2).get(month2.name()));
    }
}