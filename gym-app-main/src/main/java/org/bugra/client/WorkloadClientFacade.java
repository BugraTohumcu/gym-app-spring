package org.bugra.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.bugra.dto.client.SaveTrainerWorkload;
import org.bugra.enums.ActionType;
import org.bugra.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadClientFacade {

    private static final Logger logger = LoggerFactory.getLogger(WorkloadClientFacade.class);
    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workload-service", fallbackMethod = "workloadServiceFallback")
    public void sendWorkload(SaveTrainerWorkload saveTrainerWorkload) {
        workloadClient.saveTrainerWorkload(saveTrainerWorkload);
    }

    public SaveTrainerWorkload buildWorkloadRequest(Training training, ActionType actionType){
        return SaveTrainerWorkload.builder()
                .firstName(training.getTrainer().getUser().getFirstName())
                .lastName(training.getTrainer().getUser().getLastName())
                .username(training.getTrainer().getUser().getUsername())
                .isActive(training.getTrainer().getUser().isActive())
                .trainingDate(training.getTrainingDate())
                .actionType(actionType)
                .duration(training.getTrainingDuration())
                .build();

    }

    public void workloadServiceFallback(SaveTrainerWorkload saveTrainerWorkload, Throwable throwable) {
        logger.warn("Can not reach the workload-service for user: {} {}",
                saveTrainerWorkload.username(),
                throwable.getMessage());
    }
}