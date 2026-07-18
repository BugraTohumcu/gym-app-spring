package org.bugra.service;

import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.util.UserSession;
import org.bugra.exception.InvalidPasswordException;

public interface AuthService {

    /**
     * Validates provided username and password and sets current {@link UserSession}
     * @param loginUser wrapper that holds username and password information
     * @return {@link UserResponse}
     * @throws InvalidPasswordException if provided password does not match
     * */
    UserResponse login(LoginUser loginUser);



    /**
     * Validates provided password and changes with provided with new password
     * @param changePassword holds current and new password
     * @return true if password successfully changed false otherwise
     * @throws InvalidPasswordException if provided password does not match
     * */
    boolean changePassword(ChangePassword changePassword);
}
