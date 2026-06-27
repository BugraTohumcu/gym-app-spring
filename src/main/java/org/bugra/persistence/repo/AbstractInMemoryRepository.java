package org.bugra.persistence.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractInMemoryRepository<T, ID> implements CrudRepo<T,ID>{

    private static final Logger logger = LoggerFactory.getLogger(AbstractInMemoryRepository.class);
    private final Map<ID, T> storageMap;

    protected AbstractInMemoryRepository(Map<ID, T> storageMap) {
        this.storageMap = storageMap;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            logger.debug("findById aborted: ID parameter is null.");
            return Optional.empty();
        }

        T entity = storageMap.get(id);

        if (entity == null) {
            logger.debug("findById: The user with id: [{}] not found", id);
            return Optional.empty();
        }

        logger.debug("findById: Entity with ID [{}] successfully retrieved.", id);
        return Optional.of(entity);
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) {
            logger.debug("deleteById aborted: ID parameter is null.");
            return false;
        }

        T removedEntity = storageMap.remove(id);

        if (removedEntity == null) {
            logger.debug("deleteById: The user with id: [{}] not found", id);
            return false;
        }

        logger.debug("deleteById: The user with id: [{}] is removed successfully", id);
        return true;
    }

    @Override
    public boolean existsById(ID id) {
        return id != null && storageMap.containsKey(id);
    }
}
