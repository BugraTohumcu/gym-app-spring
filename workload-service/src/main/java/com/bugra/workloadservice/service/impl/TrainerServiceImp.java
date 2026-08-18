package com.bugra.workloadservice.service.impl;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.exception.UsernameNotFoundException;
import com.bugra.workloadservice.model.MonthlyWorkload;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.model.YearlyWorkload;
import com.bugra.workloadservice.repo.TrainerRepo;
import com.bugra.workloadservice.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainerServiceImp implements TrainerService {

    private final TrainerRepo trainerRepo;

    @Override
    @JmsListener(destination = "${app.activemq.queues.workload}", containerFactory = "jmsListenerContainerFactory")
    public void saveTrainerRecord(TrainerDto trainerDto) {
        Trainer trainer = findOrElseCreateTrainer(trainerDto);

        YearlyWorkload yearlyWorkload = findOrCreateYearlyWorkload(trainer, trainerDto.trainingDate());

        MonthlyWorkload monthlyWorkload = findOrCreateMonthlyWorkload(yearlyWorkload, trainerDto.trainingDate().getMonth());

        applyDuration(monthlyWorkload, trainerDto.actionType(), trainerDto.duration());

        trainerRepo.save(trainer);
    }

    @Override
    public TrainerWorkloadResponse getTrainerWorkload(String username) {
        Trainer trainer = trainerRepo.findByUsername(username)
                .orElseThrow(UsernameNotFoundException::new);

        Map<String, Map<String, Integer>> workloadSummary = new HashMap<>();

        trainer.getWorkloads().forEach((yearKey, yearlyWorkload) -> {
            Map<String, Integer> monthlyMap = workloadSummary.computeIfAbsent(yearKey, k -> new HashMap<>());

            yearlyWorkload.getMonths().forEach((month, monthlyWorkload) ->
                monthlyMap.merge(month, monthlyWorkload.getDuration(), Integer::sum)
            );
        });

        // create workload summary
        return TrainerWorkloadResponse.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .workloads(workloadSummary)
                .build();
    }

    private void applyDuration(MonthlyWorkload workload, ActionType actionType, int duration) {
        int delta = actionType == ActionType.ADD ? duration : -duration;
        workload.setDuration(Math.max(0, workload.getDuration() + delta));
    }

    private Trainer findOrElseCreateTrainer(TrainerDto trainerDto){
        return trainerRepo.findByUsername(trainerDto.username())
                .orElseGet(() -> {
                    Trainer newTrainer = new Trainer();
                    newTrainer.setFirstName(trainerDto.firstName());
                    newTrainer.setLastName(trainerDto.lastName());
                    newTrainer.setUsername(trainerDto.username());
                    newTrainer.setActive(trainerDto.isActive());
                    return newTrainer;
                });
    }
    private YearlyWorkload findOrCreateYearlyWorkload(Trainer trainer, LocalDate date) {
        String year = String.valueOf(date.getYear());
        return trainer.getWorkloads().computeIfAbsent(year, y -> {
            YearlyWorkload yw = new YearlyWorkload();
            yw.setYear(y);
            yw.setTrainer(trainer);
            return yw;
        });
    }

    private MonthlyWorkload findOrCreateMonthlyWorkload(YearlyWorkload yearlyWorkload, Month month) {
        return yearlyWorkload.getMonths().computeIfAbsent(month.name(), m -> {
            MonthlyWorkload mw = new MonthlyWorkload();
            mw.setDuration(0);
            return mw;
        });
    }
}
