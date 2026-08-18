package org.bugra.messaging;

import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
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
    protected Message toMessage(Object object, Session session, ObjectWriter objectWriter) throws JMSException, MessageConversionException {
        Message message = super.toMessage(object, session, objectWriter);

        String transactionId = getOrCreateTransactionId();

        message.setStringProperty(HEADER_TX_ID, transactionId);
        return message;
    }

    String getOrCreateTransactionId() {
        String transactionId = MDC.get(MDC_TX_KEY);
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            MDC.put(MDC_TX_KEY, transactionId);
        }
        return transactionId;
    }
}
