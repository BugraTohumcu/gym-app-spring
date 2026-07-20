package org.bugra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.exception.GlobalExceptionHandler;
import org.bugra.exception.UserNotFoundException;
import org.bugra.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("PATCH /user/{username}/status - Success: Active status updated")
    void updateUserStatus_shouldReturn200WhenValidRequest() throws Exception {
        String username = "john.doe";
        UpdateUserStatus userStatus = new UpdateUserStatus(false);

        doNothing().when(userService).toggleActiveStatus(username, userStatus);

        mockMvc.perform(patch("/user/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userStatus)))
                .andExpect(status().isOk());

        verify(userService).toggleActiveStatus(username, userStatus);
    }

    @Test
    @DisplayName("PATCH /user/{username}/status - Fail: Null active status (Validation Error)")
    void updateUserStatus_shouldReturn400WhenActiveIsNull() throws Exception {
        String username = "john.doe";
        UpdateUserStatus userStatus = new UpdateUserStatus(null);

        mockMvc.perform(patch("/user/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userStatus)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /user/{username}/status - Fail: User not found")
    void updateUserStatus_shouldReturn404WhenUserNotFound() throws Exception {
        String username = "nonexistent.user";
        UpdateUserStatus userStatus = new UpdateUserStatus(true);

        doThrow(new UserNotFoundException("User not found: " + username))
                .when(userService).toggleActiveStatus(username, userStatus);

        mockMvc.perform(patch("/user/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userStatus)))
                .andExpect(status().isNotFound());
    }
}