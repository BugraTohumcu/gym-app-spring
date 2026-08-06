package org.bugra.security.listener;

import org.bugra.security.UserPrincipal;
import org.bugra.service.LoginAttemptService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationEventListenerTest {

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationEventListener listener;

    @Test
    @DisplayName("Should call loginFailed when authentication fails with a valid username")
    void onFailure_shouldCallLoginFailed_whenUsernameIsNotNull() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("testuser");

        AuthenticationFailureBadCredentialsEvent event =
                new AuthenticationFailureBadCredentialsEvent(authentication, new BadCredentialsException("Bad credentials"));

        listener.onFailure(event);

        verify(loginAttemptService, times(1)).loginFailed("testuser");
    }

    @Test
    @DisplayName("Should NOT call loginFailed when authentication fails and username is null")
    void onFailure_shouldNotCallLoginFailed_whenUsernameIsNull() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        AuthenticationFailureBadCredentialsEvent event =
                new AuthenticationFailureBadCredentialsEvent(authentication, new BadCredentialsException("Bad credentials"));

        listener.onFailure(event);

        verify(loginAttemptService, never()).loginFailed(anyString());
    }

    @Test
    @DisplayName("Should call loginSucceed when authentication succeeds with UserPrincipal")
    void onSuccess_shouldCallLoginSucceed_whenPrincipalIsUserPrincipal() {
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        when(userPrincipal.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        listener.onSuccess(event);

        verify(loginAttemptService, times(1)).loginSucceed("testuser");
    }

    @Test
    @DisplayName("Should NOT call loginSucceed when principal is not an instance of UserPrincipal")
    void onSuccess_shouldNotCallLoginSucceed_whenPrincipalIsNotUserPrincipal() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("just_a_string");

        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        listener.onSuccess(event);

        verify(loginAttemptService, never()).loginSucceed(anyString());
    }
}