package com.bugra.workloadservice.steps;

import com.bugra.workloadservice.repo.TrainerRepo;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @SpyBean
    private TrainerRepo trainerRepo;
}
