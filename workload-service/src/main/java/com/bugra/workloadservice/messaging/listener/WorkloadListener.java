package com.bugra.workloadservice.messaging.listener;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.messaging.validator.JmsMessageValidator;
import com.bugra.workloadservice.service.TrainerService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadListener {

    private final MessageConverter messageConverter;
    private final TrainerService trainerService;
    private final JmsTemplate jmsTemplate;
    private final JmsMessageValidator validator;

    @JmsListener(destination = "${app.activemq.queues.workload}", containerFactory = "jmsListenerContainerFactory")
    public void onWorkloadMessageReceived(Message message) throws JMSException {
        TrainerDto trainerDto;

        // check message conversion errors
        try {
            trainerDto = (TrainerDto) messageConverter.fromMessage(message);
        }catch (MessageConversionException ex){
            log.error("Message could not be parsed: {}", ex.getMessage());
            jmsTemplate.send("workload-dlq", session -> message);
            return;
        }

        // check validation errors, if there are send to the dlq
        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(trainerDto);
        if(!violations.isEmpty()){
            String reason = validator.getReason(violations);
            log.error("The provided message has invalid fields: {}", reason);
            jmsTemplate.send("workload-dlq", session -> message);
            return;
        }

        log.info("New workload is creating for trainer: {}", trainerDto.username());
        trainerService.saveTrainerRecord(trainerDto);
    }
}
