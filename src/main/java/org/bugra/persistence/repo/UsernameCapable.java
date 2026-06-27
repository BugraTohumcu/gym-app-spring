package org.bugra.persistence.repo;

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
     * Finds the maximum ID currently present in the storage.
     */
    Long getMaxId();
}