package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Trainee;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeRepo extends UserRepo<Trainee> {
    public TraineeRepo(
            @StorageQualifier(StorageType.TRAINEE) Map<Long, Trainee> storage) {
        super(storage);
    }
}
