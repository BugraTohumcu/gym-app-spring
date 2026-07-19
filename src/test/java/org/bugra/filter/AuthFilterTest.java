package org.bugra.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bugra.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AuthService authService;

    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        authFilter = new AuthFilter();
        authFilter.setAuthService(authService);
    }

    private String encode(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @Test
    void shouldSkipAuth_whenPathContainsLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(authService);
    }

    @Test
    void shouldSkipAuth_whenPathContainsRegister() throws Exception {
        when(request.getRequestURI()).thenReturn("/register");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(authService);
    }

    @Test
    void shouldAllowRequest_whenTokenIsValid() throws Exception {
        String token = encode("john.doe", "123");

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(token);
        when(authService.isAuthenticated("john.doe", "123")).thenReturn(true);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldReturn401_whenAuthHeaderIsMissing() throws Exception {
        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn401_whenTokenIsNotValidBase64() throws Exception {
        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn("not-a-valid-base64!!");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturn401_whenDecodedTokenHasWrongFormat() throws Exception {
        String token = Base64.getEncoder().encodeToString("invalidFormat".getBytes());

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(token);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        String token = encode("john.doe", "wrong-password");

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(token);
        when(authService.isAuthenticated("john.doe", "wrong-password")).thenReturn(false);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturn401_whenAuthServiceThrowsException() throws Exception {
        String token = encode("john.doe", "123");

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(token);
        when(authService.isAuthenticated("john.doe", "123"))
                .thenThrow(new RuntimeException("DB error"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}