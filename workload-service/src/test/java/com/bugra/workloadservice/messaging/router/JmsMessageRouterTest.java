package com.bugra.workloadservice.messaging.router;

import com.bugra.workloadservice.messaging.validator.JmsMessageValidator;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.Set;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JmsMessageRouterTest {


    private record TestDto(String msg){}

    @Mock
    private JmsMessageValidator validator;

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Consumer<TestDto> testDtoConsumer;

    private final Class<TestDto> targetType = TestDto.class;

    private final String dlqDestination = "fake-dlq";

    @InjectMocks
    private JmsMessageRouter jmsMessageRouter;

    @Test
    @DisplayName("Should handle JmsExceptions when can't get the message")
    void routeMessage_shouldSendToDLQWhenUnparseable() throws JMSException {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn("invalid-message");

        when(messageConverter.fromMessage(any())).thenThrow(MessageConversionException.class);
        jmsMessageRouter.routeMessage(textMessage, targetType, dlqDestination, testDtoConsumer);

       verify(jmsTemplate).convertAndSend(eq(dlqDestination), eq("invalid-message"), any());

    }
    @Test
    @DisplayName("Should handle JmsExceptions when can't get the message")
    void routeMessage_shouldHandleJmsException() throws JMSException {
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenThrow(JMSException.class);

        when(messageConverter.fromMessage(any())).thenThrow(MessageConversionException.class);
        jmsMessageRouter.routeMessage(textMessage, targetType, dlqDestination, testDtoConsumer);

        verify(jmsTemplate).send(eq(dlqDestination), any());

    }

    @Test
    @DisplayName("Should send unparseable message to dlq when message is not text message")
    void routeMessage_shouldSendUnparseableToDlqWhenNotText() throws JMSException {
        BytesMessage bytesMessage = mock(BytesMessage.class);

        when(messageConverter.fromMessage(any())).thenThrow(MessageConversionException.class);
        jmsMessageRouter.routeMessage(bytesMessage, targetType, dlqDestination, testDtoConsumer);

        verify(jmsTemplate).send(eq(dlqDestination), any());

    }

    @Test
    @DisplayName("Should send to dlq when validation failed")
    void routeMessage_shouldSendToDLQWhenValidationFailed() throws JMSException {
        TestDto dto = new TestDto("test");
        TextMessage textMessage = mock(TextMessage.class);

        ConstraintViolation<TestDto> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<TestDto>> violations = Set.of(violation);

        when(messageConverter.fromMessage(textMessage)).thenReturn(dto);
        when(validator.validate(dto)).thenReturn(violations);
        when(validator.getReason(violations)).thenReturn("test-reason");

        jmsMessageRouter.routeMessage(textMessage, targetType, dlqDestination, testDtoConsumer);

        verify(jmsTemplate).convertAndSend(eq(dlqDestination), eq(dto), any());
    }

    @Test
    @DisplayName("Should successfully invoke provided handler")
    void route_shouldInvokeHandler() throws JMSException {
        TestDto dto = new TestDto("test");
        TextMessage textMessage = mock(TextMessage.class);

        when(messageConverter.fromMessage(textMessage)).thenReturn(dto);
        when(validator.validate(dto)).thenReturn(Set.of());

        jmsMessageRouter.routeMessage(textMessage, targetType, dlqDestination, testDtoConsumer);

        verify(testDtoConsumer).accept(dto);
    }
}