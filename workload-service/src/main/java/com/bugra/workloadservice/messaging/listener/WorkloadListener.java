package com.bugra.workloadservice.messaging.listener;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.messaging.router.JmsMessageRouter;
import com.bugra.workloadservice.service.TrainerService;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadListener {

    private final TrainerService trainerService;
    private final JmsMessageRouter router;

    @Value("${app.activemq.queues.workload-dlq}")
    private String workloadDlq;

    @JmsListener(destination = "${app.activemq.queues.workload}", containerFactory = "jmsListenerContainerFactory")
    public void onWorkloadMessageReceived(Message message){
        router.routeMessage(message, TrainerDto.class, workloadDlq, trainerService::saveTrainerRecord);
    }
}
