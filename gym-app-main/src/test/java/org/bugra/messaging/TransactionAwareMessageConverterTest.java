package org.bugra.messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionAwareMessageConverterTest {

    @InjectMocks
    private TransactionAwareMessageConverter converter;

    private static final String MDC_TX_KEY = "transactionId";

    @BeforeEach
    void setup(){
        MDC.clear();
    }

    @AfterEach
    void tearDown(){
        MDC.clear();
    }

    @Test
    @DisplayName("Should return transaction id when it is already added to MDC")
    void getOrCreateTransactionId_shouldReturnTransactionId(){
        MDC.put(MDC_TX_KEY, "test");
        String id = converter.getOrCreateTransactionId();
        assertEquals("test", id);
    }

    @Test
    @DisplayName("Should generate transaction id when it is not found in MDC")
    void getOrCreateTransactionId_shouldGenerateTransactionId(){
        String id = converter.getOrCreateTransactionId();

        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id));
    }
}