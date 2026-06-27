package org.bugra.persistence.repo;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractInMemoryRepository<T, ID> implements CrudRepo<T,ID>{

    protected final Map<ID, T> storageMap;

    public AbstractInMemoryRepository(Map<ID, T> storageMap) {
        this.storageMap = storageMap;
    }

    /**
     * Extracts the unique identifier from the given entity.
     * Used internally by {@link #save(Object)} and {@link #updateById(Object)}
     * to determine the storage key.
     * @param entity the entity to extract the ID from
     * @return the unique identifier of the entity
     */
    protected abstract ID getEntityId(T entity);

    @Override
    public T save(T entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entity can not be null");
        }

        ID id = getEntityId(entity);

        if(id == null){
            throw new IllegalArgumentException("Entity id should be auto generated");
        }

        // save entity into in-memory database
        storageMap.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> updateById(T entity) {
        ID id = getEntityId(entity);
        if(!existsById(id)) {
            return Optional.empty();
        }

        // Update existing entity
        storageMap.put(id, entity);
        return Optional.of(entity);
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
