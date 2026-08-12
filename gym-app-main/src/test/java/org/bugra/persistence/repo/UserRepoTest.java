package org.bugra.persistence.repo;

import org.bugra.enums.UserRole;
import org.bugra.exception.UserNotFoundException;
import org.bugra.model.User;
import org.bugra.persistence.BaseJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepoTest extends BaseJpaTest {

    UserRepo fakeUserRepo;

    @BeforeEach
    void init(){
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

        User fetchedUser = fakeUserRepo.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with the username: " + user.getUsername()));

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

    @Test
    @DisplayName("Should throw IllegalArgumentException when provided username is null")
    void deleteByUsername_shouldThrowWhenUsernameNull(){
        assertThrows(IllegalArgumentException.class,
                () -> fakeUserRepo.deleteByUsername(null));
    }

    @Test
    @DisplayName("Should return false when username does not exist")
    void deleteByUsername_shouldReturnFalseWhenNotExists() {
        boolean result = fakeUserRepo.deleteByUsername("test.user");
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when user is deleted successfully")
    void deleteByUsername_shouldReturnTrueWhenDeleted() {
        User user = createValidUser("john.doe", UserRole.TRAINEE);
        fakeUserRepo.save(user);
        em.flush();
        em.clear();

        boolean result = fakeUserRepo.deleteByUsername("john.doe");

        assertTrue(result);
        assertThrows(UserNotFoundException.class,
                () -> fakeUserRepo.findByUsername("john.doe"));
    }

    @Test
    @DisplayName("Should delete only the exact username, not similar ones")
    void deleteByUsername_shouldDeleteOnlyExactMatch() {
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("john.doe1", UserRole.TRAINEE);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        em.flush();
        em.clear();

        fakeUserRepo.deleteByUsername("john.doe");

        assertThrows(UserNotFoundException.class,
                () -> fakeUserRepo.findByUsername("john.doe"));

        Optional<User> remaining = fakeUserRepo.findByUsername("john.doe1");

        assertTrue(remaining.isPresent());
        assertEquals("john.doe1", remaining.get().getUsername());
    }

    @Test
    @DisplayName("Should return total number of users currently in db")
    void findUserCount_shouldReturnTotalUsers(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("jane.smith", UserRole.TRAINER);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        em.flush();
        em.clear();

        long result = fakeUserRepo.findUserCount();
        assertEquals(2, result);
    }

    @Test
    @DisplayName("Should return 0 when db is empty")
    void findUserCount_shouldReturnZero(){
        long result = fakeUserRepo.findUserCount();
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should return only number of active users ")
    void findUserCount_shouldReturnActiveUsersCount(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("jane.smith", UserRole.TRAINER);
        User user3 = createValidUser("bobby.brown", UserRole.TRAINER);

        user3.setActive(false);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        fakeUserRepo.save(user3);
        long result = fakeUserRepo.findUserCountByStatus(true);
        assertEquals(2, result);
    }

    @Test
    @DisplayName("Should return only number of passive users ")
    void findUserCount_shouldReturnPassiveUsersCount(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("jane.smith", UserRole.TRAINER);
        User user3 = createValidUser("bobby.brown", UserRole.TRAINER);

        user2.setActive(false);
        user3.setActive(false);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        fakeUserRepo.save(user3);
        long result = fakeUserRepo.findUserCountByStatus(false);
        assertEquals(2, result);
    }

    @Test
    @DisplayName("Should return the total number of trainees")
    void findUserCountByRole_shouldReturnTraineeCount(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("jane.smith", UserRole.TRAINEE);
        User user3 = createValidUser("bobby.brown", UserRole.TRAINER);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        fakeUserRepo.save(user3);

        long result = fakeUserRepo.findUserCountByRole(UserRole.TRAINEE);
        assertEquals(2,result);
    }

    @Test
    @DisplayName("Should return the total number of trainers")
    void findUserCountByRole_shouldReturnTrainerCount(){
        User user1 = createValidUser("john.doe", UserRole.TRAINEE);
        User user2 = createValidUser("jane.smith", UserRole.TRAINER);
        User user3 = createValidUser("bobby.brown", UserRole.TRAINER);

        fakeUserRepo.save(user1);
        fakeUserRepo.save(user2);
        fakeUserRepo.save(user3);

        long result = fakeUserRepo.findUserCountByRole(UserRole.TRAINER);
        assertEquals(2,result);
    }

}