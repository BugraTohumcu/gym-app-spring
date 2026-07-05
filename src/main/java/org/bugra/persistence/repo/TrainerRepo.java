package org.bugra.persistence.repo;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepo extends AbstractRepository<Trainer, Long> {

    public TrainerRepo() {
        super(Trainer.class, Trainer::getId);
    }

    public Trainer findTrainerByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);

        Root<Trainer> root = cq.from(Trainer.class);
        Join<Trainer, User> userJoin = root.join("user");

        cq.where(cb.equal(userJoin.get("username"), username));

        return entityManager.createQuery(cq)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Trainee not found with username: " + username));
    }
}
