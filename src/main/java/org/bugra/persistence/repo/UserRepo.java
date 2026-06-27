package org.bugra.persistence.repo;

import org.bugra.model.User;

import java.util.Map;
import java.util.Optional;

/**
 * <p>Abstract in-memory repository for {@link User} based entities.</p>
 * <p>Provides common CRUD operations for {@link org.bugra.model.Trainer} and {@link org.bugra.model.Trainee}.</p>
 * Extends {@link AbstractInMemoryRepository} and implements {@link UsernameCapable}.
 *
 * @param <T> the type of the entity, must extend {@link User}
 */

public abstract class UserRepo<T extends User>
        extends AbstractInMemoryRepository<T, Long>
        implements UsernameCapable {

    public UserRepo(Map<Long, T> storage) {
        super(storage);
    }

    @Override
    public T save(T entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entity can not be null");
        }

        if(entity.getId() == null){
            throw new IllegalArgumentException("Entity id should be auto generated");
        }

        // save entity into in-memory database
        storageMap.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> updateById(T entity) {
        if(!existsById(entity.getId())) {
            return Optional.empty();
        }

        // Update existing entity
        storageMap.put(entity.getId(), entity);
        return Optional.of(entity);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }
        return storageMap.values().stream()
                .anyMatch(trainee -> username.equalsIgnoreCase(trainee.getUsername()));
    }

    @Override
    public Long getMaxId() {
        return storageMap.keySet().stream()
                .max(Long::compare)
                .orElse(0L);
    }

}
