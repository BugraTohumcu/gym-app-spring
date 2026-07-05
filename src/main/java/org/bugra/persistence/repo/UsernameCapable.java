package org.bugra.persistence.repo;

import org.bugra.model.User;

import java.util.List;

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
     * @throws org.bugra.exception.UserNotFoundException if user does not exists
     * @throws IllegalArgumentException if the provided username is null
     * */
    User findByUsername(String username);

}