package com.bugra.workloadservice.filter;

import com.bugra.workloadservice.security.JwtTokenDecoder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtTokenDecoder tokenDecoder;

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(tokenDecoder);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("Should allow and set authentication when token is valid")
    void doFilterInternal_shouldAllowWhenTokenValid() throws Exception {
        String token = "valid-dummy-token";
        String username = "john.doe";

        when(request.getRequestURI()).thenReturn("/workload");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(tokenDecoder.extractUsername(token)).thenReturn(username);
        when(tokenDecoder.isValid(token)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when auth header is missing")
    void shouldPassToNextFilter_whenAuthHeaderIsMissing() throws Exception {
        when(request.getRequestURI()).thenReturn("/workload");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(tokenDecoder);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when token does not start with Bearer")
    void shouldPassToNextFilter_whenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getRequestURI()).thenReturn("/workload");
        when(request.getHeader("Authorization")).thenReturn("not-a-valid-bearer-token!!");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(tokenDecoder);
    }

    @Test
    @DisplayName("Should catch exception, set error attribute and pass to next filter when token is malformed")
    void shouldCatchException_whenDecodedTokenHasWrongFormat() throws Exception {
        String token = "malformed-token";

        when(request.getRequestURI()).thenReturn("/workload");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenDecoder.extractUsername(token)).thenThrow(new RuntimeException("Malformed token"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when token is invalid")
    void shouldNotAuthenticate_whenTokenIsInvalid() throws Exception {
        String token = "invalid-token";
        String username = "john.doe";

        when(request.getRequestURI()).thenReturn("/workload");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenDecoder.extractUsername(token)).thenReturn(username);

        when(tokenDecoder.isValid(token)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}