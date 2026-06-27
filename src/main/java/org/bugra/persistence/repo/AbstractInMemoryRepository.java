package org.bugra.persistence.repo;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractInMemoryRepository<T, ID> implements CrudRepo<T,ID>{

    protected final Map<ID, T> storageMap;

    protected AbstractInMemoryRepository(Map<ID, T> storageMap) {
        this.storageMap = storageMap;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }

        T entity = storageMap.get(id);

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity);
    }

    @Override
    public boolean deleteById(ID id) {
        if (id == null) {
            return false;
        }

        T removedEntity = storageMap.remove(id);

        return removedEntity != null;
    }

    @Override
    public boolean existsById(ID id) {
        return id != null && storageMap.containsKey(id);
    }
}
