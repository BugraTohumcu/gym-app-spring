package org.bugra.controller;


import jakarta.validation.Valid;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.response.UserResponse;
import org.bugra.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/trainee", consumes = {"application/json"}, produces = {"application/json"})
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);
    private TraineeService traineeService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainee(
            @Valid @RequestBody RegisterTrainee registerTrainee
    )
    {
            UserResponse response = traineeService.createTrainee(registerTrainee);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }
}
