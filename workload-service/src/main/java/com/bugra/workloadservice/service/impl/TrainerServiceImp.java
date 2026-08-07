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
    public void saveTrainerRecord(TrainerDto trainerDto){
        MonthlyWorkload monthlyWorkload = new MonthlyWorkload();
        monthlyWorkload.setDuration(trainerDto.duration());

        YearlyWorkload yearlyWorkload = new YearlyWorkload();
        yearlyWorkload.setYear(String.valueOf(trainerDto.trainingDate().getYear()));
        yearlyWorkload.addMonthlyWorkload(trainerDto.trainingDate().getMonth(),monthlyWorkload);

        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerDto.firstName());
        trainer.setLastName(trainerDto.lastName());
        trainer.setUsername(trainerDto.username());
        trainer.setActive(trainerDto.isActive());
        trainer.addWorkload(yearlyWorkload);

        trainerRepo.save(trainer);
    }

}
