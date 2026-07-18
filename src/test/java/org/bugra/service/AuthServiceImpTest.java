package org.bugra.service;

import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.enums.UserRole;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.impl.AuthServiceImp;
import org.bugra.util.UserSession;
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
    UserRepo mockUserRepo;

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
    @DisplayName("Should throw InvalidPasswordException when password is does not match")
    void login_shouldThrowWhenPasswordInvalid() {
        User wrongUser = new User();
        wrongUser.setUsername("john.doe");
        wrongUser.setPassword("123");

        when(mockUserRepo.findByUsername(any())).thenReturn(wrongUser);
        assertThrows(InvalidPasswordException.class,
                () -> authService.login(loginUser));
    }


    @Test
    @DisplayName("Should throw InvalidPasswordException when password does not match")
    void login_shouldReturnUserResponse() {
        User correctUser = new User();
        correctUser.setRole(UserRole.TRAINEE);
        correctUser.setUsername("john.doe");
        correctUser.setPassword("12345");

        when(mockUserRepo.findByUsername(any())).thenReturn(correctUser);

        UserResponse response = authService.login(loginUser);

        assertEquals(loginUser.username(), response.username());
        assertEquals(correctUser.getRole(), response.userRole());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password does not match")
    void changePassword_shouldThrowWhenPasswordInvalid(){
        User currentUser = new User();
        currentUser.setUsername("john.doe");
        currentUser.setPassword("123");
        UserSession.setCurrentUser(currentUser);

        // Provide invalid password
        changePassword = new ChangePassword(
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
        UserSession.setCurrentUser(currentUser);

        // Provide invalid password
        changePassword = new ChangePassword(
                "123",
                "54321"
        );

        boolean result = authService.changePassword(changePassword);

        assertEquals(changePassword.newPassword(), currentUser.getPassword());
        assertTrue(result);
    }
}