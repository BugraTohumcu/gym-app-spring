package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Trainee;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository class for {@link Trainee}
 * Inherits CRUD behaviour from {@link AbstractInMemoryRepository}, implements {@link CrudRepo} nad {@link UserBasedRepository}
 * */

@Repository
public class UserRepo extends AbstractInMemoryRepository<Trainee, Long> implements UserBasedRepository {

    public UserRepo(@StorageQualifier(StorageType.TRAINEE) Map<Long, Trainee> traineeStorage) {
        super(traineeStorage);
    }

    @Override
    public Trainee save(Trainee entity) {
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
    public Optional<Trainee> updateById(Trainee entity) {
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
