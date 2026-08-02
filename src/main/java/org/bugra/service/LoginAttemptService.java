package org.bugra.service;

public interface LoginAttemptService {
    /**
     * <p>Clear the login attempt counter</p>
     * */
    void loginSucceed(String key);

    /**
     * Increment the login attempt counter
     * */
    void loginFailed(String key);

    /**
     * <p>Check if the user is blocked</p>
     * */
    boolean isBlocked(String key);

}
