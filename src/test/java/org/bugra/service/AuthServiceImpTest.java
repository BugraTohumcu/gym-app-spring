package org.bugra.service;

import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.UserPrincipal;
import org.bugra.service.impl.AuthServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImpTest {

    @Mock
    UserRepo mockUserRepo;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthServiceImp authService;

    LoginUser loginUser;
    ChangePassword changePassword;

    @BeforeEach
    void setup(){
        loginUser = new LoginUser(
                "john.doe",
                "12345"
        );

    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void login_shouldThrowWhenPasswordInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginUser));
    }


    @Test
    @DisplayName("Should return UserResponse when login is successful")
    void login_shouldReturnUserResponse() {
        User correctUser = new User();
        correctUser.setUsername("john.doe");
        correctUser.setPassword("12345");

        UserPrincipal mockPrincipal = new UserPrincipal(correctUser);

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockPrincipal);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        UserResponse response = authService.login(loginUser);

        assertNotNull(response);
        assertEquals(loginUser.username(), response.username());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password does not match")
    void changePassword_shouldThrowWhenPasswordInvalid(){
        User currentUser = new User();
        currentUser.setUsername("john.doe");
        currentUser.setPassword("123");

        when(mockUserRepo.findByUsername(any())).thenReturn(currentUser);

        // Provide invalid password
        changePassword = new ChangePassword(
                "john.doe",
                "12345",
                "54321"
        );

        assertThrows(InvalidPasswordException.class,
                () -> authService.changePassword(changePassword));
    }

    @Test
    @DisplayName("Should return true when password change successful")
    void changePassword_shouldReturnTrueWhenPasswordChangeSuccessful(){
        User currentUser = new User();
        currentUser.setUsername("john.doe");
        currentUser.setPassword("123");

        when(mockUserRepo.findByUsername(any())).thenReturn(currentUser);
        // Provide invalid password
        changePassword = new ChangePassword(
                "john.doe",
                "123",
                "54321"
        );

        boolean result = authService.changePassword(changePassword);

        assertEquals(changePassword.newPassword(), currentUser.getPassword());
        assertTrue(result);
    }
}