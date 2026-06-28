package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingRepo extends AbstractInMemoryRepository<Training, Long> {

    public TrainingRepo() {
        super(Long::compare, 0L, Training::getId);
    }

    @Autowired
    public void setStorageMap(@StorageQualifier(StorageType.TRAINING) Map<Long, Training> storageMap) {
        super.setStorageMap(storageMap);
    }
}
