package org.bugra.service.impl;

import org.bugra.service.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryLoginAttemptService implements LoginAttemptService {

    private final int MAX_ATTEMPT = 3;
    private final ConcurrentHashMap<String, Integer> attemptCache;

    public InMemoryLoginAttemptService(){
        attemptCache = new ConcurrentHashMap<>();
    }
    @Override
    public void loginSucceed(String key) {
        attemptCache.remove(key);
    }

    @Override
    public void loginFailed(String key) {
        attemptCache.merge(key, 1, Integer::sum);
    }

    @Override
    public boolean isBlocked(String key) {
        return attemptCache.getOrDefault(key, 0) >= MAX_ATTEMPT;
    }
}
