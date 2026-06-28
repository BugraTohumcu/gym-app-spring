package org.bugra.persistence.repo;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractInMemoryRepository<T, ID> implements CrudRepo<T,ID>{

    protected final Map<ID, T> storageMap;
    private final Comparator<ID> idComparator;
    private final ID defaultId;
    private final Function<T,ID> idExtractor;

    public AbstractInMemoryRepository(
            Map<ID, T> storageMap,
            Comparator<ID> idComparator,
            ID defaultId,
            Function<T,ID> idExtractor) {
        this.storageMap = storageMap;
        this.idComparator = idComparator;
        this.defaultId = defaultId;
        this.idExtractor = idExtractor;
    }

    /**
     * Extracts the unique identifier from the given entity.
     * Used internally by {@link #save(Object)} and {@link #updateById(Object)}
     * to determine the storage key.
     * @param entity the entity to extract the ID from
     * @return the unique identifier of the entity
     */
    public ID getEntityId(T entity){
        return idExtractor.apply(entity);
    }

    /**
     * <p>Generic method for finding the maximum ID currently present in the storage.</p>
     * <p>Expects {@link  Comparator} and {@link  ID} from subclass</p>
     * @return {@link  ID } the unique id of storage defined by subclass
     */
    public ID getMaxId() {
        return storageMap.keySet().stream()
                .max(idComparator)
                .orElse(defaultId);
    }

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
