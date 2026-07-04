package org.bugra.service;

import org.bugra.model.User;
/**
 *<p>This interfaces declares user credential operations such as password and username creations
 * for {@link User} based entities</p>
 * */
public interface UserCredentialsService {
    /**
     * Generates a unique username by concatenating the first name and last name with a dot separator.
     * If a trainee or trainer with the same username already exists in the repository,
     * a sequential numeric suffix is appended to ensure uniqueness (e.g., John.Smith, John.Smith1).
     *
     * @param firstName the first name of the trainee, must not be null
     * @param lastName  the last name of the trainee, must not be null
     * @return a unique, lowercased string to be used as the profile username
     */
    String generateUsername(String firstName, String lastName);
    /**
     * Generates a random alphanumeric password with a fixed length of 10 characters.
     * This password is temporarily assigned to the new profile during creation.
     * @return a random 10-character string representing the initial password
     */
    String generateRandomPassword();
}
