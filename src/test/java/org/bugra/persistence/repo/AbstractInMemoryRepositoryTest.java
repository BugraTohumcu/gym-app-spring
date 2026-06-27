package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractInMemoryRepositoryTest {


    /**
     * <p>Test Repository class only used inAbstractInMemoryRepositoryTest.java</p>
     * <p>Extends {@link AbstractInMemoryRepository}</p>
     * */
    private static class TestRepository
        extends AbstractInMemoryRepository<Trainee, Long>{

        protected TestRepository(Map<Long, Trainee> map) {
            super(map);
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
        fakeRepository = new TestRepository(fakeStorage);
    }

    @Test
    void findById_shouldReturnEntityWhenExists(){
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        fakeStorage.put(1L, trainee);

        Optional<Trainee> result = fakeRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnNullWhenNotExists(){
        Optional<Trainee> result = fakeRepository.findById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnEmptyWhenIdIsNull(){
        Optional<Trainee> result = fakeRepository.findById(null);
        assertTrue(result.isEmpty());
    }

}