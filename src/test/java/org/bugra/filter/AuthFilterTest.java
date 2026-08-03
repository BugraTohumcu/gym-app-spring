package org.bugra.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bugra.security.JwtTokenProvider;
import org.bugra.service.impl.UserDetailsServiceImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsServiceImp userDetailsService;

    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        authFilter = new AuthFilter(jwtTokenProvider, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        // Testlerin birbirini etkilememesi için Context'i temizliyoruz
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip when path contains login")
    void doFilterInternal_shouldSkipWhenLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Should skip when path contains register")
    void doFilterInternal_shouldSkipWhenRegister() throws Exception {
        when(request.getRequestURI()).thenReturn("/register");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Should allow and set authentication when token is valid")
    void doFilterInternal_shouldAllowWhenTokenValid() throws Exception {
        String token = "valid-dummy-token";
        String username = "john.doe";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(jwtTokenProvider.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtTokenProvider.isValid(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when auth header is missing")
    void shouldPassToNextFilter_whenAuthHeaderIsMissing() throws Exception {
        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(jwtTokenProvider, userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when token does not start with Bearer")
    void shouldPassToNextFilter_whenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn("not-a-valid-bearer-token!!");

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Should catch exception, set error attribute and pass to next filter when token is malformed")
    void shouldCatchException_whenDecodedTokenHasWrongFormat() throws Exception {
        String token = "malformed-token";

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.extractUsername(token)).thenThrow(new RuntimeException("Malformed token"));

        authFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should pass to next filter without authenticating when token is invalid")
    void shouldNotAuthenticate_whenTokenIsInvalid() throws Exception {
        String token = "invalid-token";
        String username = "john.doe";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getRequestURI()).thenReturn("/trainee/john.doe");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        when(jwtTokenProvider.isValid(token, userDetails)).thenReturn(false);

        authFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}