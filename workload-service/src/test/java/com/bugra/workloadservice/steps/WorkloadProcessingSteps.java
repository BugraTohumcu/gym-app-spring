package com.bugra.workloadservice.steps;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.model.Trainer;
import com.bugra.workloadservice.repo.TrainerRepo;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadProcessingSteps {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainerRepo trainerRepo;

    @Value("${app.activemq.queues.workload}")
    private String workloadQueue;

    private LocalDate testDate;

    @Given("the database and workload queue are completely empty")
    public void theDatabaseAndQueueAreEmpty(){
        trainerRepo.deleteAll();

        jmsTemplate.setReceiveTimeout(30);

        while(jmsTemplate.receive(workloadQueue) != null){
            // consume all messages
        }
    }

    @When("a valid workload message for trainer {string} with {int} minutes is sent to the queue")
    public void aValidWorkloadMessageIsSent(String username, int duration) {

        testDate = LocalDate.now().plusDays(5);
        TrainerDto trainerDto = TrainerDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username(username)
                .isActive(true)
                .trainingDate(testDate)
                .duration(duration)
                .actionType(ActionType.ADD)
                .build();

        jmsTemplate.convertAndSend(workloadQueue, trainerDto);
    }

    @Then("the message should be processed without any errors")
    public void messageShouldBeProcessed(){
        // wait message to be processed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @And("the database should reflect {int} minutes of workload for {string}")
    public void databaseShouldReflectTheRecord(int expectedDuration, String expectedUsername){
        Optional<Trainer> trainer = trainerRepo.findByUsername(expectedUsername);

        assertTrue(trainer.isPresent());
        String year  = String.valueOf(testDate.getYear());
        String month = testDate.getMonth().name();
        int actualDuration = trainer.get().getWorkloads().get(year).get(month);

        assertEquals(expectedDuration, actualDuration);
    }
}

