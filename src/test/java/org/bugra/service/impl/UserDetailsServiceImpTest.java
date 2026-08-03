package org.bugra.service.impl;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.UserPrincipal;
import org.bugra.service.LoginAttemptService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImpTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private UserDetailsServiceImp userDetailsService;

    @Test
    @DisplayName("Should throw LockedException when user account is locked")
    void loadUserByUsername_shouldThrowWhenUserLocked() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(true);

        assertThrows(LockedException.class,
                () -> userDetailsService.loadUserByUsername(anyString()));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_shouldThrowWhenUserNotFound() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepo.findByUsername(anyString())).thenThrow(UserNotFoundException.class);
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(anyString()));
    }

    @Test
    @DisplayName("Should return UserDetails when user found and account is non-blocked")
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = new User();

        user.setUsername("john.doe");
        user.setPassword("secret123");
        user.setRole(UserRole.TRAINEE);

        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(userRepo.findByUsername(anyString())).thenReturn(user);

        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername("john.doe");

        assertEquals("john.doe", principal.getUsername());
        assertEquals("secret123", principal.getPassword());
        assertEquals(
                new SimpleGrantedAuthority("ROLE_"+UserRole.TRAINEE.name()),
                principal.getAuthorities().stream().toList().getFirst()
        );
    }
}