package org.bugra.security.listener;


import lombok.RequiredArgsConstructor;
import org.bugra.security.UserPrincipal;
import org.bugra.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final LoginAttemptService loginAttemptService;


    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event){
        String username = (String) event.getAuthentication().getPrincipal();

        if (username != null) {
            loginAttemptService.loginFailed(username);
        }
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            loginAttemptService.loginSucceed(userPrincipal.getUsername());
        }
    }
}
