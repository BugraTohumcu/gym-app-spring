package org.bugra.service.impl;


import jakarta.transaction.Transactional;
import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.UserResponse;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImp implements AuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImp.class);
    private UserRepo userRepo;


    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserResponse login(LoginUser loginUser) {
        logger.info("User with username: {} is trying to login" , loginUser.username());
        User fetchedUser = userRepo.findByUsername(loginUser.username());

        // Password check
        if(!fetchedUser.getPassword().equals(loginUser.password())){
            logger.warn("The provided password for user with username {} is invalid", loginUser.username());
            throw new InvalidPasswordException();
        }

        logger.info("User with username: {} is successfully logged in" , loginUser.username());
        return new UserResponse(
                fetchedUser.getUsername(),
                fetchedUser.getPassword()
        );
    }

    @Transactional
    @Override
    public boolean changePassword(ChangePassword changePassword) {
        logger.info("The user with username: {} is changing password",
                changePassword.username()
                );

        User currentUser = userRepo.findByUsername(changePassword.username());
        // Password check
        if(!currentUser.getPassword().equals(changePassword.currentPassword())){
            logger.warn("The provided password for user with username: {} is invalid",
                    currentUser.getUsername());
            throw new InvalidPasswordException();
        }

        // set new password and update db
        currentUser.setPassword(changePassword.newPassword());
        userRepo.update(currentUser);

        logger.info("User with username: {} successfully updated password" , currentUser.getUsername());
        return true;
    }

    @Override
    public boolean isAuthenticated(String username, String password) {
        User user = userRepo.findByUsername(username);
        return user.getPassword().equals(password);
    }
}
