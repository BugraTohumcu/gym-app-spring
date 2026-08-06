package org.bugra.monitor.health;

import lombok.RequiredArgsConstructor;
import org.bugra.model.TrainingType;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Responsible for indicating training type table.</p>
 * <p>DOWN if the table is empty, UP otherwise</p>
 * */
@Component
@RequiredArgsConstructor
public class TrainingTypeRepoIndicator implements HealthIndicator {

    private final TrainingTypeRepo typeRepo;

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }


    @Override
    public Health health() {
        List<TrainingType> types = typeRepo.findAllTypes();
        if(types.isEmpty()) {
            return Health.down()
                    .withDetail("trainingTypeTable", "empty")
                    .build();
        }
        return Health.up()
                .withDetail("trainingTypeTable", "non-empty")
                .build();
    }
}
