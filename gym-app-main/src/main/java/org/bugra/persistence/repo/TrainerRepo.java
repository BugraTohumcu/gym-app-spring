package org.bugra.persistence.repo;

import jakarta.persistence.criteria.*;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepo extends AbstractRepository<Trainer, Long> {

    public TrainerRepo() {
        super(Trainer.class, Trainer::getId);
    }

    public Optional<Trainer> findTrainerByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);

        Root<Trainer> root = cq.from(Trainer.class);
        Join<Trainer, User> userJoin = root.join("user");
        root.fetch("trainees",JoinType.LEFT);

        cq.where(cb.equal(userJoin.get("username"), username));

        return entityManager.createQuery(cq)
                .getResultList()
                .stream()
                .findFirst();
    }

    public List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
        Root<Trainer> root = cq.from(Trainer.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Trainee> traineeRoot = subquery.from(Trainee.class);
        Join<Trainee, Trainer> trainerJoin = traineeRoot.join("trainers");
        Join<Trainee, User> userJoin = traineeRoot.join("user");

        subquery.select(trainerJoin.get("id"));
        subquery.where(cb.equal(userJoin.get("username"), traineeUsername));

        cq.where(cb.not(root.get("id").in(subquery)));

        return entityManager.createQuery(cq).getResultList();
    }

    public List<Trainer> findAllByUsernames(List<String> usernames){
        if(usernames == null || usernames.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
        Root<Trainer> root = cq.from(Trainer.class);

        Join<Trainer, User> userJoin = root.join("user");

        cq.where(userJoin.get("username").in(usernames));

        return entityManager.createQuery(cq).getResultList();
    }
}
