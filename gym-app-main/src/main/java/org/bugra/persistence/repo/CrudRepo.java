package org.bugra.persistence.repo;

import java.util.Optional;

/**
 * Generic repository interface providing core CRUD operations
 *
 * @param <T> the domain type the repository manages
 * @param <ID> the type of id of the entity
 * */

public interface CrudRepo<T, ID> {

    /**
     * <p>Saves a given entity.</p>
     * @param entity entity to be saved must not be null
     * @return the saved entity
     * */
    T save(T entity);

    /**
     * <p>Retrieves the entity with a provided generic id field</p>
     * @param id the unique id for entity
     * @return an {@link Optional} containing found entity, or {@link Optional#empty()} if entity does not found
     * */
    Optional<T> findById(ID id);


    /**
     * <p>Updates an existing entity. Verifies the existence of the entity.</p>
     * @param entity the entity object containing updated data and a valid ID
     * @return the updated and persisted entity
     * @throws IllegalArgumentException if the trainee profile does not exist in the storage
     */
    Optional<T> update(T entity);


    /**
     * <p>Deletes the entity with the provided id</p>
     * @param id the unique id for entity
     * @return true if entity deleted successfully, or false if entity does not found
     * */
    boolean deleteById(ID id);


    /**
     * <p>Checks whether the entity with the given id exists</p
     * @param id the unique id for entity
     * @return true if entity is found, false otherwise
     * */
    boolean existsById(ID id);
}
