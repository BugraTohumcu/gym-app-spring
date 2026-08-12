package org.bugra.persistence.repo;

import jakarta.persistence.criteria.*;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.Trainee;
import org.bugra.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class TraineeRepo extends AbstractRepository<Trainee, Long> {

    public TraineeRepo() {
        super(Trainee.class, Trainee::getId);
    }


    public Optional<Trainee> findTraineeByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainee> cq = cb.createQuery(Trainee.class);

        Root<Trainee> root = cq.from(Trainee.class);
        Join<Trainee, User> userJoin = root.join("user");

        root.fetch("trainers", JoinType.LEFT);

        cq.where(cb.equal(userJoin.get("username"), username));

        return entityManager.createQuery(cq)
                .getResultStream()
                .findFirst();
    }
}
