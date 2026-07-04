package org.bugra.persistence.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Optional;
import java.util.function.Function;

/**
 * <p>Generic abstract repository base for all repository classes.</p>
 * <p>Handles basic CRUD operations</p>
 * <p>Implements {@link CrudRepo}</p>
 * */
public abstract class AbstractRepository<T, ID> implements CrudRepo<T,ID>{


    protected EntityManager entityManager;

    // Make class immutable and prevent type erasure
    private final Class<T> entityClass;
    private final Function<T, ID> idExtractor;

    public AbstractRepository(
            Class<T> entityClass,
            Function<T, ID> idExtractor) {
        this.entityClass = entityClass;
        this.idExtractor = idExtractor;
    }

    /**
     * Extracts the unique identifier from the given entity.
     * Used internally by {@link #save(Object)} and {@link #update(Object)}
     * to determine the storage key.
     * @param entity the entity to extract the ID from
     * @return the unique identifier of the entity
     */
    public ID getEntityId(T entity){
        return idExtractor.apply(entity);
    }

    @Override
    public T save(T entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entity can not be null");
        }

        // if it's not exist save, merge otherwise
        if(getEntityId(entity) == null){
            entityManager.persist(entity);
            return entity;
        }

        return entityManager.merge(entity);
    }

    @Override
    public Optional<T> update(T entity) {
        ID id = getEntityId(entity);

        if(id == null || entityManager.find(entityClass, id) == null) {
            return Optional.empty();
        }

        return Optional.of(entityManager.merge(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        T entity = entityManager.find(entityClass, id);

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity);
    }

    @Override
    public boolean deleteById(ID id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        T entity = entityManager.find(entityClass, id);

        if(entity != null){
            entityManager.remove(entity);
            return true;
        }

        return  false;
    }

    @Override
    public boolean existsById(ID id) {
        if (id == null) return false;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);

        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("id"), id));

        return entityManager.createQuery(cq).getSingleResult() > 0;
    }


    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
