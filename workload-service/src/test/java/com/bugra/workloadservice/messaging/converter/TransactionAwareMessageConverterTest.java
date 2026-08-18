package com.bugra.workloadservice.messaging.converter;


import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionAwareMessageConverterTest {
    @Mock
    private TextMessage message;

    @InjectMocks
    private TransactionAwareMessageConverter converter;


    private static final String HEADER_TX_ID = "X-Transaction-ID";
    private static final String MDC_TX_KEY = "transactionId";

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should add transaction id when id provided in headers")
    void setTransactionId_shouldAddTransactionId() throws JMSException {
        when(message.getStringProperty(HEADER_TX_ID)).thenReturn("test-1234");

        converter.setTransactionId(message);

        String id = MDC.get(MDC_TX_KEY);
        assertEquals("test-1234", id);
    }

    @Test
    @DisplayName("Should create and transaction id when id is not provided")
    void setTransactionId_shouldCreateAndAddTransactionId() throws JMSException {
        when(message.getStringProperty(HEADER_TX_ID)).thenReturn(null);

        converter.setTransactionId(message);

        String id = MDC.get(MDC_TX_KEY);

        assertDoesNotThrow(() -> java.util.UUID.fromString(id));
    }
}