package com.bugra.workloadservice.service.impl;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.model.MonthlyWorkload;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.model.YearlyWorkload;
import com.bugra.workloadservice.repo.TrainerRepo;
import com.bugra.workloadservice.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerServiceImp implements TrainerService {

    private final TrainerRepo trainerRepo;
    @Override
    public void saveTrainerRecord(TrainerDto trainerDto) {

        // create new trainee if there is no record with provided username
        Trainer trainer = trainerRepo.findByUsername(trainerDto.username())
                .orElseGet(() -> {
                    Trainer newTrainer = new Trainer();
                    newTrainer.setFirstName(trainerDto.firstName());
                    newTrainer.setLastName(trainerDto.lastName());
                    newTrainer.setUsername(trainerDto.username());
                    newTrainer.setActive(trainerDto.isActive());
                    return newTrainer;
                });


        // set workload
        YearlyWorkload yearlyWorkload = new YearlyWorkload();
        yearlyWorkload.setYear(String.valueOf(trainerDto.trainingDate().getYear()));

        MonthlyWorkload monthlyWorkload = new MonthlyWorkload();
        monthlyWorkload.setDuration(trainerDto.duration());

        yearlyWorkload.addMonthlyWorkload(trainerDto.trainingDate().getMonth(), monthlyWorkload);

        trainer.addWorkload(yearlyWorkload);

        trainerRepo.save(trainer);
    }
}
