package org.bugra.persistence.repo;

import jakarta.persistence.criteria.*;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingRepo extends AbstractRepository<Training, Long> {

    public TrainingRepo() {
        super(Training.class, Training::getId);
    }


    public List<Training> findByTraineeCriteria(TraineeTrainingFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> root = cq.from(Training.class);

        TrainingJoins joins = buildJoins(root);
        List<Predicate> predicates = buildTraineePredicates(cb, root, joins, filter);

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Training> findByTrainerCriteria(TrainerTrainingFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> root = cq.from(Training.class);

        TrainingJoins joins = buildJoins(root);
        List<Predicate> predicates = buildTrainerPredicates(cb, root, joins, filter);

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }


    private TrainingJoins buildJoins(Root<Training> root) {
        return new TrainingJoins(
                root.join("trainee").join("user"),
                root.join("trainer").join("user"),
                root.join("trainingType")
        );
    }

    private List<Predicate> buildTraineePredicates(CriteriaBuilder cb,
                                                   Root<Training> root,
                                                   TrainingJoins joins,
                                                   TraineeTrainingFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(usernameEquals(cb, joins.traineeUser(), filter.traineeUsername()));

        if (filter.fromDate() != null) predicates.add(dateAfter(cb, root, filter.fromDate()));
        if (filter.toDate() != null) predicates.add(dateBefore(cb, root, filter.toDate()));
        if (isPresent(filter.trainerName())) predicates.add(nameLike(cb, joins.trainerUser(), filter.trainerName()));
        if (isPresent(filter.trainingType())) predicates.add(typeEquals(cb, joins.trainingType(), filter.trainingType()));

        return predicates;
    }

    private List<Predicate> buildTrainerPredicates(CriteriaBuilder cb,
                                                   Root<Training> root,
                                                   TrainingJoins joins,
                                                   TrainerTrainingFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(usernameEquals(cb, joins.trainerUser(), filter.trainerUsername()));

        if (filter.fromDate() != null) predicates.add(dateAfter(cb, root, filter.fromDate()));
        if (filter.toDate() != null) predicates.add(dateBefore(cb, root, filter.toDate()));
        if (isPresent(filter.traineeName())) predicates.add(nameLike(cb, joins.traineeUser(), filter.traineeName()));

        return predicates;
    }

    private Predicate usernameEquals(CriteriaBuilder cb, Join<?, User> userJoin, String username) {
        return cb.equal(userJoin.get("username"), username);
    }

    private Predicate dateAfter(CriteriaBuilder cb, Root<Training> root, LocalDate fromDate) {
        return cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate);
    }

    private Predicate dateBefore(CriteriaBuilder cb, Root<Training> root, LocalDate toDate) {
        return cb.lessThanOrEqualTo(root.get("trainingDate"), toDate);
    }

    private Predicate nameLike(CriteriaBuilder cb, Join<?, User> userJoin, String name) {
        return cb.like(cb.lower(userJoin.get("firstName")), "%" + name.toLowerCase() + "%");
    }

    private Predicate typeEquals(CriteriaBuilder cb, Join<?, TrainingType> typeJoin, String type) {
        return cb.equal(cb.lower(typeJoin.get("trainingTypeName")), type.toLowerCase());
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }


    private record TrainingJoins(
            Join<?, User> traineeUser,
            Join<?, User> trainerUser,
            Join<?, TrainingType> trainingType
    ) {}
}