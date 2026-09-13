package com.bugra.workloadservice.steps;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.enums.ActionType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageRoutingSteps {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${app.activemq.queues.workload}")
    private String workloadQueue;

    @Value("${app.activemq.queues.workload-dlq}")
    private String dlqQueue;

    private Message receivedDlqMessage;

    @Given("the DLQ is completely empty")
    public void theDlqIsCompletelyEmpty() {
        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(dlqQueue) != null) {
            // consume all messages
        }
    }

    @When("an invalid message with an empty username is sent to the main workload queue")
    public void anInvalidMessageWithAnEmptyUsernameIsSent() {
        // invalid message with empty username
        TrainerDto invalidDto = new TrainerDto(
                "John",
                "Doe",
                "",
                true,
                LocalDate.now().plusDays(5),
                60,
                ActionType.ADD
        );

        jmsTemplate.convertAndSend(workloadQueue, invalidDto);
    }

    @Then("the message should not be processed by the main system")
    public void theMessageShouldNotBeProcessed() {
        // pause for delegating invalid message to dlq
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @And("the message should be routed to the DLQ")
    public void theMessageShouldBeRoutedToTheDlq() {
        jmsTemplate.setReceiveTimeout(5000);
        receivedDlqMessage = jmsTemplate.receive(dlqQueue);

        assertNotNull(receivedDlqMessage, "Message was not routed to the DLQ!");
    }

    @And("the message in the DLQ should contain the header {string}")
    public void theMessageInTheDlqShouldContainTheHeader(String headerName) throws Exception {
        String dlqReason = receivedDlqMessage.getStringProperty(headerName);

        assertNotNull(dlqReason, headerName + " header is missing from the DLQ message!");
        assertTrue(dlqReason.contains("Username is required"), "The DLQ reason header does not contain the expected validation error detail!");
    }
}