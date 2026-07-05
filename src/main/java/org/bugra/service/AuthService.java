package org.bugra.service;

import org.bugra.dto.LoginUser;
import org.bugra.dto.UserResponse;
import org.bugra.util.UserSession;

public interface AuthService {

    /**
     * Validates provided username and password and sets current {@link UserSession}
     * @param loginUser wrapper that holds username and password information
     * @return {@link UserResponse}
     * */
    UserResponse login(LoginUser loginUser);
}
