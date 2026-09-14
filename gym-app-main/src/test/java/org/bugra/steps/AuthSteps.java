package org.bugra.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.bugra.enums.UserRole;
import org.bugra.model.User;
import org.bugra.persistence.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private ResultActions response;

    @Given("a registered user exists with username {string} and password {string}")
    public void aRegisteredUserExists(String username, String password) {
        transactionTemplate.execute(status -> {
            if (!userRepo.existsByUsername(username)) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName("Test");
                user.setLastName("User");
                user.setActive(true);
                user.setRole(UserRole.TRAINEE);
                userRepo.save(user);
            }
            return null;
        });
    }

    @When("the user attempts to log in with username {string} and password {string}")
    public void theUserAttemptsToLogIn(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        response = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)));
    }

    @Then("the system should return a successful response")
    public void theSystemShouldReturnASuccessfulResponse() throws Exception {
        response.andExpect(status().isOk());
    }

    @When("an unauthenticated user attempts to access a protected endpoint")
    public void anUnauthenticatedUserAttemptsToAccessAProtectedEndpoint() throws Exception {
        response = mockMvc.perform(get("/trainer/johndoe")
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("the system should return an unauthorized error response")
    public void theSystemShouldReturnAnUnauthorizedErrorResponse() throws Exception {
        response.andExpect(status().isUnauthorized());
    }
}