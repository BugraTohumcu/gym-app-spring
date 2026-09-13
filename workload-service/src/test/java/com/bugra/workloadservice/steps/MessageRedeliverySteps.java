package com.bugra.workloadservice.steps;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.enums.ActionType;
import com.bugra.workloadservice.repo.TrainerRepo;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class MessageRedeliverySteps {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainerRepo trainerRepo;

    @Value("${app.activemq.queues.workload}")
    private String workloadQueue;

    private Message receivedMessage;

    @Given("the default DLQ {string} is empty")
    public void theDefaultDlqEmpty(String dlqName) {
        jmsTemplate.setReceiveTimeout(30);
        while (jmsTemplate.receive(dlqName) != null){
            // consume all messages
        }
    }

    @When("a valid message causing an internal error")
    public void aValidMessageCausingAnInternalError(){

        doThrow(new RuntimeException("Mock exception"))
                .when(trainerRepo).save(argThat(t -> "error".equals(t.getUsername())));

        TrainerDto trainerDto = TrainerDto.builder()
                .firstName("John")
                .lastName("Doe")
                .username("error")
                .isActive(true)
                .trainingDate(LocalDate.now().plusDays(1))
                .duration(120)
                .actionType(ActionType.ADD)
                .build();

        jmsTemplate.convertAndSend(workloadQueue, trainerDto);
    }

    @Then("the system should retry the message")
    public void theSystemShouldRetryTheMessage(){
        // wait for retries
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(trainerRepo, atLeast(2)).save(argThat(t -> "error".equals(t.getUsername())));
    }

    @And("the message should finally be routed to the default dlq {string}")
    public void theMessageShouldFinallyBeRoutedToTheDefaultDlq(String dlqName){
        jmsTemplate.setReceiveTimeout(5000);
        receivedMessage = jmsTemplate.receive(dlqName);

        assertNotNull(receivedMessage, "The message was not routed to the default DLQ after retries!");
    }
}
