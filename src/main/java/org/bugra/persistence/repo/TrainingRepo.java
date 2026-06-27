package org.bugra.persistence.repo;

import org.bugra.annotation.StorageQualifier;
import org.bugra.enums.StorageType;
import org.bugra.model.Training;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingRepo extends AbstractInMemoryRepository<Training, Long> {

    public TrainingRepo(
            @StorageQualifier(StorageType.TRAINING) Map<Long, Training> storageMap) {
        super(storageMap);
    }

    @Override
    protected Long getEntityId(Training entity) {
        return entity.getId();
    }
}
