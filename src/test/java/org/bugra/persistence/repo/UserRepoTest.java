package org.bugra.persistence.repo;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepoTest extends BaseJpaTest {

    UserRepo fakeUserRepo;

    @BeforeEach
    void setup(){
        fakeUserRepo = new UserRepo();
        fakeUserRepo.setEntityManager(em);
    }

    @Test
    @DisplayName("Should return false when username is null")
    void existsByUsername_shouldReturnFalseWhenUsernameNull() {
        boolean result = fakeUserRepo.existsByUsername(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when username exists")
    void existsByUsername_shouldReturnTrueWhenUsernameExists() {
        User user = createValidUser("john.doe1", UserRole.TRAINEE);

        fakeUserRepo.save(user);
        em.flush();
        em.clear();

        boolean result = fakeUserRepo.existsByUsername(user.getUsername());
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when username does not exists")
    void existsByUsername_shouldReturnFalseWhenUsernameNotExists() {
        String username = "John.Doe1";

        boolean result = fakeUserRepo.existsByUsername(username);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should Throw IllegalArgumentException if base provided name is null")
    void findUsernameStartingWith_shouldThrowWhenBaseNameNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fakeUserRepo.findUsernameStartingWith(null));
    }

    @Test
    @DisplayName("Should return empty list when basename does not exists")
    void findUsernameStartingWith_shouldReturnEmptyListWhenBasenameNotExists(){

        List<String> result = fakeUserRepo.findUsernameStartingWith("test");
        assertEquals(
                0,
                result.size()
        );
    }


    @Test
    @DisplayName("Should return username list when basename does exists")
    void findUsernameStartingWith_shouldReturnListWhenBaseNameExists(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("john.doe1", UserRole.TRAINEE);
        User user3 = createValidUser("john-doe1", UserRole.TRAINEE);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        fakeUserRepo.save(user3);
        em.flush();
        em.clear();

        List<String> result = fakeUserRepo.findUsernameStartingWith("john.doe");

        assertTrue(result.contains("john.doe"));
        assertTrue(result.contains("john.doe1"));
        assertFalse(result.contains("john-doe1"));
    }


    @Test
    @DisplayName("Should return user if username does exists")
    void findByUsername_shouldReturnUser(){
        User user = createValidUser("john.doe", UserRole.TRAINEE);
        fakeUserRepo.save(user);
        em.flush();
        em.clear();

        User fetchedUser = fakeUserRepo.findByUsername(user.getUsername());

        assertEquals("john.doe", fetchedUser.getUsername());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when provided username is null")
    void findByUsername_shouldThrowWhenUsernameNull(){
        assertThrows(IllegalArgumentException.class,
                () -> fakeUserRepo.findByUsername(null));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when username does not exists")
    void findByUsername_shouldThrowWhenUsernameNotExists(){
        assertThrows(UserNotFoundException.class,
                () -> fakeUserRepo.findByUsername("test"));
    }

}