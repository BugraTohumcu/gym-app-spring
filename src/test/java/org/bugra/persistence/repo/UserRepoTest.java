package org.bugra.persistence.repo;

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
        User user = new User();
        user.setUsername("john.doe1");

        User savedUser = fakeUserRepo.save(user);

        boolean result = fakeUserRepo.existsByUsername(savedUser.getUsername());
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
        User user1 = new User();
        user1.setUsername("john.doe");

        User user2 = new User();
        user2.setUsername("john.doe1");

        User user3 = new User();
        user3.setUsername("john-doe1");

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);

        List<String> result = fakeUserRepo.findUsernameStartingWith("john.doe");

        assertTrue(result.contains("john.doe"));
        assertTrue(result.contains("john.doe1"));
    }


}