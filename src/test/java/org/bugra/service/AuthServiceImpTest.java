package org.bugra.service;

import org.bugra.dto.LoginUser;
import org.bugra.dto.UserResponse;
import org.bugra.enums.UserRole;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImpTest {

    @Mock
    UserCredentialsService mockUserService;

    @InjectMocks
    AuthServiceImp authService;

    LoginUser loginUser;

    @BeforeEach
    void setup(){
        loginUser = new LoginUser(
                "john.doe",
                "12345"
        );

    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password is does not match")
    void login_shouldThrowWhenPasswordInvalid() {
        User wrongUser = new User();
        wrongUser.setUsername("john.doe");
        wrongUser.setPassword("123");

        when(mockUserService.findByUsername(any())).thenReturn(wrongUser);
        assertThrows(InvalidPasswordException.class,
                () -> authService.login(loginUser));
    }


    @Test
    @DisplayName("Should throw InvalidPasswordException when password is does not match")
    void login_shouldReturnUserResponse() {
        User correctUser = new User();
        correctUser.setRole(UserRole.TRAINEE);
        correctUser.setUsername("john.doe");
        correctUser.setPassword("12345");

        when(mockUserService.findByUsername(any())).thenReturn(correctUser);

        UserResponse response = authService.login(loginUser);

        assertEquals(loginUser.username(), response.username());
        assertEquals(correctUser.getRole(), response.userRole());
    }
}