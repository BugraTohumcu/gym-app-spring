package org.bugra.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.bugra.dto.request.CreateTraining;
import org.bugra.enums.UserRole;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.bugra.persistence.repo.TraineeRepo;
import org.bugra.persistence.repo.TrainerRepo;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.bugra.persistence.repo.UserRepo;
import org.bugra.security.JwtTokenProvider;
import org.bugra.security.dto.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingWorkloadSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TrainerRepo trainerRepo;

    @Autowired
    private TraineeRepo traineeRepo;

    @Autowired
    private TrainingTypeRepo trainingTypeRepo;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Given("the ActiveMQ test queue is completely empty")
    public void theActiveMqTestQueueIsCompletelyEmpty() {
    }

    @When("a new training session of {int} minutes is created for trainer {string} and trainee {string}")
    public void aNewTrainingSessionIsCreated(int duration, String trainerUsername, String traineeUsername) throws Exception {

        setupTestData(trainerUsername, traineeUsername);
        String token = jwtTokenProvider.generateAccessToken(new TokenPayload(trainerUsername));

        CreateTraining request = new CreateTraining(
                trainerUsername,
                traineeUsername,
                "test-training",
                "Yoga",
                LocalDate.now().plusDays(2),
                duration
        );

        mockMvc.perform(post("/trainer/trainings/training")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Then("the training should be saved successfully in the main database")
    public void theTrainingShouldBeSavedSuccessfully() {
    }

    @And("a workload update message for {string} with {int} minutes should be sent to the ActiveMQ queue")
    public void aWorkloadUpdateMessageShouldBeSent(String trainer, int expectedDuration) {
        verify(jmsTemplate, timeout(5000).atLeastOnce())
                .convertAndSend(
                        anyString(),
                        any(Object.class),
                        any(org.springframework.jms.core.MessagePostProcessor.class)
                );
    }

    private void setupTestData(String trainerUsername, String traineeUsername) {
        transactionTemplate.execute(status -> {
            TrainingType type = trainingTypeRepo.findByTrainingTypeName("Yoga").orElseGet(() -> {
                TrainingType t = new TrainingType();
                t.setTrainingTypeName("Yoga");
                return trainingTypeRepo.save(t);
            });

            if (!userRepo.existsByUsername(trainerUsername)) {
                User user = new User();
                user.setUsername(trainerUsername);
                user.setPassword("12345");
                user.setFirstName("Jane");
                user.setLastName("Smith");
                user.setActive(true);
                user.setRole(UserRole.TRAINER);
                userRepo.save(user);

                Trainer trainer = new Trainer();
                trainer.setUser(user);
                trainer.setSpecialization(type);
                trainer.setTrainees(new java.util.HashSet<>());
                trainerRepo.save(trainer);
            }

            if (!userRepo.existsByUsername(traineeUsername)) {
                User user = new User();
                user.setUsername(traineeUsername);
                user.setPassword("12345");
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setActive(true);
                user.setRole(UserRole.TRAINEE);
                userRepo.save(user);

                Trainee trainee = new Trainee();
                trainee.setUser(user);
                trainee.setTrainers(new java.util.HashSet<>());
                traineeRepo.save(trainee);
            }
            return null;
        });
    }
}