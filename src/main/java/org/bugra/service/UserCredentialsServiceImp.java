package org.bugra.service;

import org.bugra.util.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserCredentialsServiceImp implements UserCredentialsService {

    private PasswordGenerator passwordGenerator;
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialsServiceImp.class);

    @Override
    public String generateUsername(String firstName,
                                   String lastName,
                                   Function<String, Boolean> existByName) {
        // Null and blank check for credentials
        if (firstName == null || lastName == null ||
                firstName.isBlank() || lastName.isBlank()) {
            logger.warn("First name or last name cannot be null or empty");
            throw new IllegalArgumentException("First name or last name cannot be null or empty");
        }

        // Create base username
        String baseName = (firstName.trim() + "." + lastName.trim()).toLowerCase();
        StringBuilder builder = new StringBuilder(baseName);

        // Check if username is taken
        // For the simplicity I've used linear search.
        // Better approach may be second level indexing eg: {"john.doe": 0}
        int counter = 1;
        while (existByName.apply(builder.toString())) {
            // Reset username
            builder.setLength(0);

            builder.append(baseName).append(counter);
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
}
