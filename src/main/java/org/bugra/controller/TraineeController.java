package org.bugra.controller;


import jakarta.validation.Valid;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.UpdateTrainee;
import org.bugra.dto.response.TraineeProfileResponse;
import org.bugra.dto.response.UserResponse;
import org.bugra.mapper.TraineeResponseMapper;
import org.bugra.model.Trainee;
import org.bugra.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/trainee")
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);
    private TraineeService traineeService;
    private TraineeResponseMapper traineeResponseMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainee(
            @Valid @RequestBody RegisterTrainee registerTrainee
    )
    {
            logger.info("New trainee is creating with name: {} {}",registerTrainee.firstName(), registerTrainee.lastName());
            Trainee trainee = traineeService.createTrainee(registerTrainee);
            UserResponse response = new UserResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword());
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @PathVariable(value = "username") String username
    ){
        logger.info("The trainee profile with username {} is fetching", username);
        Trainee trainee = traineeService.getTraineeByUsername(username);
        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @Valid @RequestBody UpdateTrainee updateTrainee
            ){

        logger.info("The trainee with username {} is updating profile", updateTrainee.username());
        Trainee trainee = traineeService.updateTrainee(updateTrainee);
        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setTraineeResponseMapper(TraineeResponseMapper traineeResponseMapper) {
        this.traineeResponseMapper = traineeResponseMapper;
    }
}
