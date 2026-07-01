package org.bugra.persistence.repo;

import org.bugra.model.User;

import java.util.Map;

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

    public UserRepo() {
        super(Long::compare, 0L, User::getId);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) {
            return false;
        }
        return storageMap.values().stream()
                .anyMatch(trainee -> username.equalsIgnoreCase(trainee.getUsername()));
    }

}
