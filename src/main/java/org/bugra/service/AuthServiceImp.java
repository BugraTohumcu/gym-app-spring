package org.bugra.service;


import org.bugra.dto.LoginUser;
import org.bugra.dto.UserResponse;
import org.bugra.exception.InvalidPasswordException;
import org.bugra.model.User;
import org.bugra.util.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImp implements AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthServiceImp.class);
    private UserCredentialsService userService;


    @Autowired
    public void setUserService(UserCredentialsService userService) {
        this.userService = userService;
    }

    @Override
    public UserResponse login(LoginUser loginUser) {
        logger.info("User with username {} is trying to login" , loginUser.username());
        User fetchedUser = userService.findByUsername(loginUser.username());

        if(!fetchedUser.getPassword().equals(loginUser.password())){
            logger.warn("The provided password for user with username {} is invalid", loginUser.username());
            throw new InvalidPasswordException();
        }

        UserSession.setCurrentUser(fetchedUser);

        return new UserResponse(
                fetchedUser.getUsername(),
                fetchedUser.getRole(),
                fetchedUser.getFirstName() + " " + fetchedUser.getLastName()
        );
    }
}
