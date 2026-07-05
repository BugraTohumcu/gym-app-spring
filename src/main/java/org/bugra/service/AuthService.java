package org.bugra.service;

import org.bugra.dto.LoginUser;
import org.bugra.model.User;
import org.bugra.util.UserSession;

public interface AuthService {

    /**
     * Validates provided username and password and sets current {@link UserSession}
     * @param loginUser wrapper that holds username and password information
     * @return {@link User} object that retrieved from database
     * */
    User login(LoginUser loginUser);
}
