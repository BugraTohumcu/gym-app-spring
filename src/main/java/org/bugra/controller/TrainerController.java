package org.bugra.controller;


import jakarta.validation.Valid;
import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.response.UserResponse;
import org.bugra.model.Trainer;
import org.bugra.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);
    private TrainerService trainerService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainee(
            @Valid @RequestBody RegisterTrainer registerTrainee
    )
    {
        logger.info("New trainee is creating with name: {} {}",registerTrainee.firstName(), registerTrainee.lastName());
        Trainer trainer = trainerService.createTrainer(registerTrainee);
        UserResponse response = new UserResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }
}
