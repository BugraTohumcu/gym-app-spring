package org.bugra.service.impl;

import jakarta.transaction.Transactional;
import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private UserRepo userRepo;


    @Transactional
    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with provided username: " + username));
    }

    @Transactional
    @Override
    public boolean deleteByUsername(String username) {
        if (username == null) {
            logger.warn("Provided username is null");
            throw new IllegalArgumentException("Username can not be null");
        }

        logger.info("Deleting user with the username: {}", username);

        if(!userRepo.deleteByUsername(username)){
            logger.info("User with the username: {} does not exists", username);
            throw new UserNotFoundException();
        }

        logger.info("Deleted user with the username: {}", username);
        return true;
    }

    @Override
    @Transactional
    public void toggleActiveStatus(String username, UpdateUserStatus userStatus) {
        if (username == null) {
            logger.warn("Provided username is null");
            throw new IllegalArgumentException("Username can not be null");
        }

        logger.info("Toggling active status for user: {}", username);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with provided username: " + username));

        boolean newStatus = userStatus.status();
        user.setActive(newStatus);
        userRepo.update(user);

        logger.info("User: {} active status changed to: {}", username, newStatus);
    }


    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
