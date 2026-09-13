package com.bugra.workloadservice.steps;

import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.repo.TrainerRepo;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerRepoSteps {

    @Autowired
    private TrainerRepo trainerRepo;

    @Given("the database is completely empty")
    public void theDatabaseIsCompletelyEmpty() {
        trainerRepo.deleteAll();
    }

    @When("a new trainer with username {string} and first name {string} is saved to the system")
    public void aNewTrainerIsSavedToTheSystem(String username, String firstName) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName("Doe");
        trainer.setActive(true);

        trainerRepo.save(trainer);
    }

    @Then("the total number of trainers in the database should be {int}")
    public void theTotalNumberOfTrainersShouldBe(int expectedCount) {
        long actualCount = trainerRepo.count();
        assertEquals(expectedCount, actualCount, "Trainer count in the database does not match!");
    }

    @Then("the trainer with username {string} should be verified to have the first name {string}")
    public void theTrainerShouldBeVerified(String username, String expectedFirstName) {
        Optional<Trainer> foundTrainer = trainerRepo.findByUsername(username);

        assertTrue(foundTrainer.isPresent(), "Trainer not found in the database!");
        assertEquals(expectedFirstName, foundTrainer.get().getFirstName(), "Trainer first name does not match!");
    }
}