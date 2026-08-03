package org.bugra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.GlobalExceptionHandler;
import org.bugra.exception.UserNotFoundException;
import org.bugra.monitor.metric.AuthMetric;
import org.bugra.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MeterRegistry meterRegistry;

    private AuthMetric authMetric;

    @Mock
    private AuthService authService;

    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(){
        meterRegistry = new SimpleMeterRegistry();
        authMetric = new AuthMetric(meterRegistry);

        authController = new AuthController(authService, authMetric);

        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /login - Success")
    void login_shouldReturn200AndLogin() throws Exception {
        LoginUser loginUser = new LoginUser(
                "john.doe",
                "123"
        );

        UserResponse userResponse = new UserResponse(
                "john.doe",
                "123",
                "dummy_token"
        );

        when(authService.login(any())).thenReturn(userResponse);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /login - Fail")
    void login_shouldReturn404AndThrow() throws Exception {
        LoginUser loginUser = new LoginUser(
                "john.doe",
                "123"
        );

        when(authService.login(any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /login - Success")
    void changePassword_shouldReturn200() throws Exception {
        ChangePassword changePassword = new ChangePassword(
          "john.doe",
          "123",
          "1234"
        );

        when(authService.changePassword(any())).thenReturn(true);

        mockMvc.perform(put("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePassword)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /login - Fail")
    void changePassword_shouldReturn404() throws Exception {
        ChangePassword changePassword = new ChangePassword(
                "john.doe",
                "123",
                "1234"
        );

        when(authService.changePassword(any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePassword)))
                .andExpect(status().isNotFound());
    }
}