package org.bugra.persistence.repo;

import org.bugra.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Specialized repository interface for entities that possess a username and require sequential ID generation.
 * Separated from the global CrudRepo to respect the Interface Segregation Principle.
 */
public interface UsernameCapable {

    /**
     * Checks if a specific username is already taken by any entity in the storage.
     */
    boolean existsByUsername(String username);


    /**
     * <p>Retrieves all the usernames with the provided base username (eg: "john.doe")</p>
     * @param basename the base username to retrieve similar usernames
     * @return the {@link List} of all similar usernames
     * */
    List<String> findUsernameStartingWith(String basename);


    /**
     * Retrieves a specific user through the provided username
     * @param username the unique username
     * @return the specific {@link User} entity
     * @throws org.bugra.exception.UserNotFoundException if user does not exit
     * @throws IllegalArgumentException if the provided username is null
     * */
    Optional<User> findByUsername(String username);


    /**
     * Deletes a user with provided username
     * @param username a unique, lowercased string to be used as the profile username
     * @return true if deleted, false otherwise
     * @throws IllegalArgumentException if the provided username is null
     * */
    boolean deleteByUsername(String username);

}