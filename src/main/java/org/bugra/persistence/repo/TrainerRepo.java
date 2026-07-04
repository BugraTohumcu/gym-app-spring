package org.bugra.persistence.repo;

import org.bugra.model.Trainer;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepo extends AbstractRepository<Trainer, Long> {

    public TrainerRepo() {
        super(Trainer.class, Trainer::getId);
    }

}
