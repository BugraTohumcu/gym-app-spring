package com.bugra.workloadservice.messaging.listener;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.exception.MissingFieldException;
import com.bugra.workloadservice.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadListener {

    private final TrainerService trainerService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = "${app.activemq.queues.workload}", containerFactory = "jmsListenerContainerFactory")
    public void onWorkloadMessageReceived(TrainerDto trainerDto){
        try{
            log.info("New workload is creating for trainer: {}", trainerDto.username());
            trainerService.saveTrainerRecord(trainerDto);
        }catch (MissingFieldException ex){
            log.error("The provided message has missing fields: {}", ex.getMessage());

            jmsTemplate.convertAndSend("workload-dlq", trainerDto);
        }catch (Exception ex){
            log.error("Something went wrong {}", ex.getMessage());
            throw ex;
        }
    }
}
