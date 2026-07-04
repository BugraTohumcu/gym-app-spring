package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.springframework.stereotype.Repository;


@Repository
public class TraineeRepo extends AbstractRepository<Trainee, Long> {

    public TraineeRepo() {
        super(Trainee.class, Trainee::getId);
    }
}
