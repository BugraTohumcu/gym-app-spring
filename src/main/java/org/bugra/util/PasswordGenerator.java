package org.bugra.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

/**
 * <p>Random password generator class</p>
 * <p>Responsobile for creating fixed 10 chars length  alphanumeric passwords</p>
 * */
@Component
public class PasswordGenerator {

    private static final String PASSWORD_ALLOW_BASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * <p>Generates 10 chars length alphanumeric password</p>
     * @return {@link String} randomly created user password
     * */
    public String generate() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(randomIndex));
        }
        return password.toString();
    }
}