package org.bugra.monitor.health;

import org.bugra.model.TrainingType;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;


import java.util.List;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepoIndicatorTest {

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @InjectMocks
    private TrainingTypeRepoIndicator trainingTypeRepoIndicator;

    @Test
    @DisplayName("Should status be UP when training type table is non-empty")
    void health_shouldBeUpWhenTableIsNonEmpty(){
        when(trainingTypeRepo.findAllTypes()).thenReturn(List.of(new TrainingType()));

        Health health = trainingTypeRepoIndicator.health();
        assertEquals("UP", health.getStatus().toString());
        assertEquals("non-empty", health.getDetails().get("trainingTypeTable").toString());
    }

    @Test
    @DisplayName("Should status be DOWN when training type table is empty")
    void health_shouldBeDOWNWhenTableIsEmpty(){
        when(trainingTypeRepo.findAllTypes()).thenReturn(List.of());

        Health health = trainingTypeRepoIndicator.health();
        assertEquals("DOWN", health.getStatus().toString());
        assertEquals("empty", health.getDetails().get("trainingTypeTable").toString());
    }
}