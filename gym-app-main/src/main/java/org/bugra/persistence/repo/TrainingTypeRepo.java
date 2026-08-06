package org.bugra.persistence.repo;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.bugra.model.TrainingType;
import org.hibernate.dialect.function.ListaggStringAggEmulation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepo extends AbstractRepository<TrainingType, Long> {

    public TrainingTypeRepo() {
        super(TrainingType.class, TrainingType::getId);
    }

    public Optional<TrainingType> findByTrainingTypeName(String trainingTypeName) {
        if (trainingTypeName == null || trainingTypeName.isBlank()) {
            throw new IllegalArgumentException("Training type name cannot be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TrainingType> cq = cb.createQuery(TrainingType.class);
        Root<TrainingType> root = cq.from(TrainingType.class);

        cq.where(cb.equal(root.get("trainingTypeName"), trainingTypeName));

        return entityManager.createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public List<TrainingType> findAllTypes() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        TypedQuery<TrainingType> query = entityManager.createQuery("SELECT t from TrainingType t", TrainingType.class);

        return query.getResultList();
    }
}