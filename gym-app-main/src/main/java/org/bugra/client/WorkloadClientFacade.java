package org.bugra.client;

import lombok.RequiredArgsConstructor;
import org.bugra.dto.client.SaveTrainerWorkload;
import org.bugra.enums.ActionType;
import org.bugra.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadClientFacade {

    private static final Logger logger = LoggerFactory.getLogger(WorkloadClientFacade.class);
    private final JmsTemplate jmsTemplate;

    public void sendWorkload(SaveTrainerWorkload saveTrainerWorkload) {
        logger.info("Sending workload for trainer: {}", saveTrainerWorkload.username());
        String transactionId = MDC.get("transactionId");
        jmsTemplate.convertAndSend("workload-service-queue", saveTrainerWorkload, message -> {
            message.setStringProperty("X-Transaction-ID", transactionId);
            return message;
        });
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

}