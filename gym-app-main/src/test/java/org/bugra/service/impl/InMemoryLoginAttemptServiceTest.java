package org.bugra.service.impl;

import org.bugra.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;


    private final int MAX_ATTEMPT = 3;
    private final int BLOCK_DURATION = 5;
    @BeforeEach
    void setup(){
        loginAttemptService = new InMemoryLoginAttemptService();
    }

    @Test
    @DisplayName("Should reset the attempts when login successful")
    void loginSucceed_shouldRestAttempts() {
        String key = "test";

        ConcurrentHashMap<String, Integer> internalCache =
                (ConcurrentHashMap<String, Integer>) ReflectionTestUtils.getField(loginAttemptService, "attemptCache");

        internalCache.put(key, 2);

        loginAttemptService.loginSucceed(key);

        assertFalse(internalCache.contains(key));

    }

    @Test
    @DisplayName("Should increment the attempts when login failed")
    void loginFailed_shouldIncrementAttempts() {
        String key = "test";

        ConcurrentHashMap<String, Integer> internalCache =
                (ConcurrentHashMap<String, Integer>) ReflectionTestUtils.getField(loginAttemptService, "attemptCache");

        internalCache.put(key, 2);
        loginAttemptService.loginFailed(key);


        assertEquals(3, internalCache.get(key));
    }

    @Test
    @DisplayName("Should return true if attempts exceed 3")
    void isBlocked_shouldReturnTrue() {
        String key = "test";

        ConcurrentHashMap<String, Integer> attemptCache =
                (ConcurrentHashMap<String, Integer>) ReflectionTestUtils.getField(loginAttemptService, "attemptCache");

        ConcurrentHashMap<String, LocalDateTime> blockCache =
                (ConcurrentHashMap<String, LocalDateTime>) ReflectionTestUtils.getField(loginAttemptService, "blockCache");

        attemptCache.put(key, 3);
        blockCache.put(key, LocalDateTime.now().plusMinutes(5));

        assertTrue(loginAttemptService.isBlocked(key));
    }

    @Test
    @DisplayName("Should block user after three login failure")
    void isBlocked_shouldBlockUser() {
        String key = "test";

        loginAttemptService.loginFailed(key);
        assertFalse(loginAttemptService.isBlocked(key));

        loginAttemptService.loginFailed(key);
        assertFalse(loginAttemptService.isBlocked(key));

        loginAttemptService.loginFailed(key);
        assertTrue(loginAttemptService.isBlocked(key));
    }
}