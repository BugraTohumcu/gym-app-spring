package org.bugra.persistence.repo;

import org.bugra.model.Training;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingRepo extends AbstractRepository<Training, Long> {

    public TrainingRepo() {
        super(Training.class, Training::getId);
    }
}
