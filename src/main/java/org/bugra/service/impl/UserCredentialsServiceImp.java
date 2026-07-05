package org.bugra.service.impl;

import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.service.UserCredentialsService;
import org.bugra.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserCredentialsServiceImp implements UserCredentialsService {

    private UserRepo userRepo;
    private PasswordGenerator passwordGenerator;
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsServiceImp.class);

    @Override
    public String generateUsername(String firstName, String lastName) {
        // Null and blank check for credentials
        if (firstName == null || lastName == null ||
                firstName.isBlank() || lastName.isBlank()) {
            logger.warn("First name or last name cannot be null or empty");
            throw new IllegalArgumentException("First name or last name cannot be null or empty");
        }

        // Create base username
        String baseName = (firstName.trim() + "." + lastName.trim()).toLowerCase();
        StringBuilder builder = new StringBuilder(baseName);

        Set<String> takenNames;
        {
            // fetch the list of usernames that uses the basename
            List<String> existingUsernames = userRepo.findUsernameStartingWith(baseName);
            takenNames = new HashSet<>(existingUsernames);
        }

        // Return base name if it is unique
        if(!takenNames.contains(baseName)){
            return baseName;
        }

        // Check if username is taken and apply the username creation strategy
        int counter = 1;
        while (takenNames.contains(builder.toString())){
            builder.setLength(baseName.length());
            builder.append(counter);
            counter++;
        }

        return builder.toString();
    }

    @Override
    public String generateRandomPassword() {
        return passwordGenerator.generate();
    }

    @Autowired
    void setPasswordGenerator(PasswordGenerator passwordGenerator){
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
}
