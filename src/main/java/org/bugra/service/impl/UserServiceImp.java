package org.bugra.service.impl;

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


    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void toggleActiveStatus(String username) {
        if (username == null) {
            logger.warn("Provided username is null");
            throw new IllegalArgumentException("Username can not be null");
        }

        logger.info("Toggling active status for user: {}", username);
        User user = userRepo.findByUsername(username);

        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        userRepo.update(user);

        logger.info("User: {} active status changed to: {}", username, newStatus);
    }


    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
