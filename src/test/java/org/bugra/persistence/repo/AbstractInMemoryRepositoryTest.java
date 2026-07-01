package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class AbstractInMemoryRepositoryTest {


    /**
     * <p>Test Repository class only used inAbstractInMemoryRepositoryTest.java</p>
     * <p>Extends {@link AbstractInMemoryRepository}</p>
     * */
    private static class TestRepository
        extends AbstractInMemoryRepository<Trainee, Long>{

        protected TestRepository() {
            super(Long::compare, 0L, Trainee::getId);
        }

        @Override
        public Trainee save(Trainee entity) {
            return null;
        }

        @Override
        public Optional<Trainee> updateById(Trainee entity) {
            return Optional.empty();
        }
    }


    private TestRepository fakeRepository;
    private Map<Long, Trainee> fakeStorage;

    @BeforeEach
    void setUp() {
        fakeStorage = new ConcurrentHashMap<>();
        fakeRepository = new TestRepository();
        fakeRepository.setStorageMap(fakeStorage);
    }

    @Test
    @DisplayName("Should return the correct entity if entity is exits")
    void findById_shouldReturnEntityWhenExists(){
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        fakeStorage.put(1L, trainee);

        Optional<Trainee> result = fakeRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return null if entity does not exits")
    void findById_shouldReturnNullWhenNotExists(){
        Optional<Trainee> result = fakeRepository.findById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return optional empty if id is null")
    void findById_shouldReturnEmptyWhenIdIsNull(){
        Optional<Trainee> result = fakeRepository.findById(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return false if provided id is null")
    void deleteById_shouldReturnFalseWhenIdNull(){
        boolean result = fakeRepository.deleteById(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false if entity does not exits")
    void deleteById_shouldReturnFalseWhenNotExist(){
        boolean result = fakeRepository.deleteById(1L);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true if entity is deleted")
    void deleteById_shouldReturnTrueIfDeleted(){
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        fakeStorage.put(1L, trainee);

        boolean result = fakeRepository.deleteById(1L);

        assertTrue(result);
        assertNull(fakeStorage.get(1L));
    }

    @Test
    @DisplayName("Should return true if id is exist")
    void exitsById_ShouldReturnTrueIfIdExists(){
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        fakeStorage.put(1L, trainee);

        boolean result = fakeRepository.existsById(1L);
        assertTrue(result);
    }


    @Test
    @DisplayName("Should return false if id does not exist")
    void exitsById_ShouldReturnFalseIfIdNotExists(){
        boolean result = fakeRepository.existsById(1L);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false if id is null")
    void exitsById_ShouldReturnTrueIfIdNull(){
        boolean result = fakeRepository.existsById(null);
        assertFalse(result);
    }
}