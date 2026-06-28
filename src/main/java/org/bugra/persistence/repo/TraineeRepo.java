package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TraineeRepo extends UserRepo<Trainee> {

    public TraineeRepo() {
        super();
    }

    @Autowired
    public void setStorageMap(@StorageQualifier(StorageType.TRAINEE) Map<Long, Trainee> storageMap) {
        super.setStorageMap(storageMap);
    }
}
