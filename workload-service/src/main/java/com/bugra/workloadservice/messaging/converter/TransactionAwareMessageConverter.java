package com.bugra.workloadservice.messaging.converter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.MDC;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;

import java.util.UUID;


/**
 * Responsible for sending adding transaction id to message property if the transaction id does not exist
 * */
public class TransactionAwareMessageConverter extends MappingJackson2MessageConverter {

    private static final String HEADER_TX_ID = "X-Transaction-ID";
    private static final String MDC_TX_KEY = "transactionId";


    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        setTransactionId(message);

        return super.fromMessage(message);
    }

    void setTransactionId(Message message) throws JMSException {
        String tx = message.getStringProperty(HEADER_TX_ID);

        if (tx == null) {
            tx = UUID.randomUUID().toString();
        }

        MDC.put(MDC_TX_KEY, tx);
    }
}
