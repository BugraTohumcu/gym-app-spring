package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainerRepo extends UserRepo<Trainer> {

    public TrainerRepo() { super(); }


    @Autowired
    public void setStorageMap(@StorageQualifier(StorageType.TRAINER) Map<Long, Trainer> storageMap) {
        super.setStorageMap(storageMap);
    }
}
