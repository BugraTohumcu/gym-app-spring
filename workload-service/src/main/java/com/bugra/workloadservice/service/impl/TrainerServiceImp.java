package com.bugra.workloadservice.service.impl;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.exception.UsernameNotFoundException;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.repo.TrainerRepo;
import com.bugra.workloadservice.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImp implements TrainerService {

    private final TrainerRepo trainerRepo;

    @Override
    public void saveTrainerRecord(TrainerDto trainerDto) {
        log.info("New Trainer record saving for trainer: {}", trainerDto.username());
        Trainer trainer = findOrElseCreateTrainer(trainerDto);

        String year = String.valueOf(trainerDto.trainingDate().getYear());
        Month month = trainerDto.trainingDate().getMonth();

        Map<String, Map<String,Integer>> workload = trainer.getWorkloads();

        Map<String, Integer> monthlyWorkload = workload.computeIfAbsent(year, k -> new HashMap<>());

        applyDuration(monthlyWorkload, trainerDto.actionType(), trainerDto.duration(), month);

        trainerRepo.save(trainer);
    }

    @Override
    public TrainerWorkloadResponse getTrainerWorkload(String username) {
        log.info("Trainer record fetching for trainer: {}", username);

        Trainer trainer = trainerRepo.findByUsername(username)
                .orElseThrow(UsernameNotFoundException::new);

        // create workload summary
        return TrainerWorkloadResponse.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.isActive())
                .workloads(trainer.getWorkloads())
                .build();
    }

    private void applyDuration(Map<String, Integer> workload, ActionType actionType, int duration, Month month) {
        // prevent negative duration
        int delta = actionType == ActionType.ADD ? duration : -duration;
        workload.put(
                month.name(),
                Math.max(0, workload.getOrDefault(month.name(), 0) + delta)
        );
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
}
