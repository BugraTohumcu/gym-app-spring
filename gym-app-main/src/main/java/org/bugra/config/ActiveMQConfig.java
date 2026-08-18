package org.bugra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bugra.messaging.TransactionAwareMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActiveMQConfig {

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        MappingJackson2MessageConverter converter =
                new TransactionAwareMessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("SaveTrainingWorkload", org.bugra.dto.client.SaveTrainerWorkload.class);

        converter.setTypeIdMappings(typeIdMappings);
        converter.setObjectMapper(objectMapper);

        return converter;
    }
}
