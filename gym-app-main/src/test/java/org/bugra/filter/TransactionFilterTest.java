package org.bugra.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionFilterTest {

    private final TransactionFilter filter = new TransactionFilter();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void doFilterInternal_shouldSetTransactionIdInMdcDuringDownstreamCall() throws Exception {
        doAnswer(invocation -> {
            String txId = MDC.get("transactionId");
            assertNotNull(txId, "transactionId is not set");
            assertFalse(txId.isBlank());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldClearMdcAfterRequestCompletes() throws Exception {
        filter.doFilter(request, response, filterChain);

        assertNull(MDC.get("transactionId"), "MDC does not cleaned after request");
    }

    @Test
    void doFilterInternal_shouldClearMdcWhenDownstreamThrows() throws Exception {
        doThrow(new RuntimeException("downstream failure"))
                .when(filterChain).doFilter(request, response);

        assertThrows(RuntimeException.class,
                () -> filter.doFilter(request, response, filterChain));

        assertNull(MDC.get("transactionId"));
    }

    @Test
    void doFilterInternal_shouldGenerateDifferentTransactionIdForEachRequest() throws Exception {
        String[] capturedIds = new String[2];

        doAnswer(inv -> {
            capturedIds[0] = MDC.get("transactionId");
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilter(request, response, filterChain);

        doAnswer(inv -> {
            capturedIds[1] = MDC.get("transactionId");
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilter(request, response, filterChain);

        assertNotEquals(capturedIds[0], capturedIds[1], "Each request must have different id");
    }
}