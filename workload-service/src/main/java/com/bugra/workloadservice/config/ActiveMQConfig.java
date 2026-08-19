package com.bugra.workloadservice.config;

import com.bugra.workloadservice.messaging.converter.TransactionAwareMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ActiveMQConfig {

    private JmsTemplate jmsTemplate;

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory
            (
                    ConnectionFactory connectionFactory,
                    DefaultJmsListenerContainerFactoryConfigurer configurer
            )
    {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();

        factory.setErrorHandler( t -> {
            log.error("[Error Handler] - uncaught error {}", t.getMessage());
        });

        configurer.configure(factory, connectionFactory);

        return factory;
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        MappingJackson2MessageConverter converter =
                new TransactionAwareMessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("SaveTrainingWorkload", com.bugra.workloadservice.dto.request.TrainerDto.class);

        converter.setTypeIdMappings(typeIdMappings);
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}

