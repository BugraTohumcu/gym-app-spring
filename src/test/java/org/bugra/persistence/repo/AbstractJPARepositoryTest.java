package org.bugra.persistence.repo;

import org.bugra.model.Trainee;
import org.bugra.model.User;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AbstractJPARepositoryTest extends BaseJpaTest {


    /**
     * <p>Test Repository class only used in AbstractRepositoryTest.java</p>
     * <p>Extends {@link AbstractRepository}</p>
     * */
    private static class TestRepository
        extends AbstractRepository<Trainee, Long> {

        protected TestRepository() {
            super(Trainee.class, Trainee::getId);
        }

        @Override
        public Trainee save(Trainee entity) {
            return super.save(entity);
        }

        @Override
        public Optional<Trainee> update(Trainee entity) {
            return super.update(entity);
        }
    }


    private TestRepository fakeRepository;

    @BeforeEach
    void init() {
        fakeRepository = new TestRepository();
        fakeRepository.setEntityManager(em);
    }

    @Test
    @DisplayName("Should return the correct entity if entity is exits")
    void findById_shouldReturnEntityWhenExists(){
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);

        Trainee savedTrainee = fakeRepository.save(trainee);
        em.flush();
        em.clear();

        Optional<Trainee> result = fakeRepository.findById(savedTrainee.getId());
        assertTrue(result.isPresent());

        assertEquals(savedTrainee.getId(), result.get().getId());
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
        assertThrows(IllegalArgumentException.class,
                () -> fakeRepository.findById(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if provided id is null")
    void deleteById_shouldThrowWhenIdNull(){
        assertThrows(IllegalArgumentException.class,
                () -> fakeRepository.deleteById(null));
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
        trainee.setUser(new User());
        Trainee savedTrainee = fakeRepository.save(trainee);
        em.flush();
        em.clear();

        boolean result = fakeRepository.deleteById(savedTrainee.getId());

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return true if id is exist")
    void exitsById_ShouldReturnTrueIfIdExists(){
        Trainee trainee = new Trainee();
        trainee.setUser(new User());
        Trainee savedTrainee = fakeRepository.save(trainee);
        em.flush();
        em.clear();

        boolean result = fakeRepository.existsById(savedTrainee.getId());
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