package org.bugra.persistence.repo;

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
}