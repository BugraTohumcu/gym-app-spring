package org.bugra.persistence.repo;

import org.bugra.model.TrainingType;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeRepoTest extends BaseJpaTest {

    private TrainingTypeRepo fakeRepo;

    @BeforeEach
    void init(){
        fakeRepo = new TrainingTypeRepo();
        fakeRepo.setEntityManager(em);
    }

    @Test
    @DisplayName("Should return correct type name")
    void findByTrainingTypeName_shouldReturnName() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("test");
        fakeRepo.save(type);

        Optional<TrainingType> typeFound = fakeRepo.findByTrainingTypeName("test");

        assertTrue(typeFound.isPresent());
        assertEquals("test", typeFound.get().getTrainingTypeName());
    }

    @ParameterizedTest(name = "Throws IllegalArgumentException when training type name is null or blank")
    @NullAndEmptySource
    void findByTrainingTypeName_shouldThrowWhenNameInvalid(String trainingTypeName) {
        assertThrows(IllegalArgumentException.class,
                () -> fakeRepo.findByTrainingTypeName(trainingTypeName));
    }


    @Test
    @DisplayName("Should return all training type names")
    void findAllTypes_shouldReturnAllNames() {
        TrainingType type1 = new TrainingType();
        type1.setTrainingTypeName("test1");
        fakeRepo.save(type1);

        TrainingType type2 = new TrainingType();
        type2.setTrainingTypeName("test2");
        fakeRepo.save(type2);

        List<TrainingType> trainingTypeList = fakeRepo.findAllTypes();
        List<String> fetchedNames = trainingTypeList.stream()
                .map(TrainingType::getTrainingTypeName)
                .toList();

        assertEquals(2, fetchedNames.size());
        assertTrue(fetchedNames.containsAll(List.of("test1", "test2")));
    }
}