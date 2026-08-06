package org.bugra.service.impl;

import org.bugra.service.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryLoginAttemptService implements LoginAttemptService {

    private final int MAX_ATTEMPT = 3;
    private final int BLOCK_DURATION = 5;
    private final ConcurrentHashMap<String, Integer> attemptCache;
    private final ConcurrentHashMap<String, LocalDateTime> blockCache;

    public InMemoryLoginAttemptService(){
        attemptCache = new ConcurrentHashMap<>();
        blockCache = new ConcurrentHashMap<>();
    }
    @Override
    public void loginSucceed(String key) {
        attemptCache.remove(key);
        blockCache.remove(key);
    }

    @Override
    public void loginFailed(String key) {
        int attempt = attemptCache.merge(key, 1, Integer::sum);
        if(attempt >= MAX_ATTEMPT){
            blockCache.put(key, LocalDateTime.now().plusMinutes(BLOCK_DURATION));
        }
    }

    @Override
    public boolean isBlocked(String key) {
        LocalDateTime unBlockTime = blockCache.get(key);

        if(unBlockTime != null){
            if(LocalDateTime.now().isBefore(unBlockTime)){
                return true;
            }else{
                attemptCache.remove(key);
                blockCache.remove(key);
                return false;
            }
        }

        return false;
    }
}
