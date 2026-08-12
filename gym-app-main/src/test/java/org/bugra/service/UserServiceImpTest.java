package org.bugra.service;

import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.impl.UserServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    UserRepo userRepo;

    @InjectMocks
    UserServiceImp userService;

    @Test
    @DisplayName("Should throw IllegalArgumentException when username is null")
    void toggleActiveStatus_shouldThrowWhenUsernameNull(){
        UpdateUserStatus userStatus = new UpdateUserStatus(true);
        assertThrows(IllegalArgumentException.class,
                () -> userService.toggleActiveStatus(null,userStatus));
    }

    @Test
    @DisplayName("Should toggle active status from true to false")
    void toggleActiveStatus_shouldToggleFromTrueToFalse() {
        User user = new User();
        user.setActive(true);
        UpdateUserStatus userStatus = new UpdateUserStatus(false);
        when(userRepo.findByUsername("test")).thenReturn(Optional.of(user));

        userService.toggleActiveStatus("test", userStatus);

        assertFalse(user.isActive());
        verify(userRepo).update(user);
    }

    @Test
    @DisplayName("Should toggle active status from false to true")
    void toggleActiveStatus_shouldToggleFromFalseToTrue() {
        User user = new User();
        user.setActive(false);
        UpdateUserStatus userStatus = new UpdateUserStatus(true);
        when(userRepo.findByUsername("test")).thenReturn(Optional.of(user));

        userService.toggleActiveStatus("test",userStatus);

        assertTrue(user.isActive());
        verify(userRepo).update(user);
    }

}