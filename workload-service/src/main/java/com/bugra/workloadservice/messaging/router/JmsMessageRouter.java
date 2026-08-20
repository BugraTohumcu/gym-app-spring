package com.bugra.workloadservice.messaging.router;

import com.bugra.workloadservice.messaging.validator.JmsMessageValidator;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsMessageRouter {
    private final MessageConverter messageConverter;
    private final JmsMessageValidator validator;
    private final JmsTemplate jmsTemplate;

    private final String REASON_HEADER = "X-DLQ-Reason";

    public <T> void routeMessage(Message message, Class<T> targetType, String dlqDestination, Consumer<T> handler) {
        T dto;

        // handle message conversion errors,
        try {
            dto = targetType.cast(messageConverter.fromMessage(message));
        } catch (MessageConversionException | ClassCastException | JMSException ex) {
            log.error("Message could not be parsed: {}", ex.getMessage());
            sendUnparseableMessage(message, dlqDestination, ex.getMessage());
            return;
        }

        // handle dto validation errors
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String reason = validator.getReason(violations);
            log.error("The provided message has invalid fields: {}", reason);

            jmsTemplate.convertAndSend(dlqDestination, dto, newMessage -> {
                newMessage.setStringProperty(REASON_HEADER, reason);
                return newMessage;
            });
            return;
        }

        // don't catch errors, delegate to default dlq
        handler.accept(dto);
    }

    /**
     * Send unparseable text messages to given dlq
     * */
    private void sendUnparseableMessage(Message message, String dlqDestination, String reason) {
        try{
            if(message instanceof TextMessage textMessage) {
                jmsTemplate.convertAndSend(dlqDestination, textMessage.getText(), newMessage -> {
                    newMessage.setStringProperty(REASON_HEADER, reason);
                    return newMessage;
                });
            }else {
                jmsTemplate.send(dlqDestination, session -> message);
            }
        }catch (JMSException ex){
            log.error("Invalid message couldn't read");
            jmsTemplate.send(dlqDestination, session -> message);
        }
    }
}
