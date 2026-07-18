package org.bugra.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RestLoggingFilterTest {

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternal_shouldDoFilterChain() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/trainee");
        when(response.getStatus()).thenReturn(200);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldThrowFromDownstream() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/trainee");
        when(response.getStatus()).thenReturn(500);

        doThrow(new RuntimeException("downstream failure"))
                .when(filterChain).doFilter(request, response);

        assertThrows(RuntimeException.class,
                () -> filter.doFilter(request, response, filterChain));
    }
}