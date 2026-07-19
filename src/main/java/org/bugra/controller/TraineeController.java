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
            UserResponse response = traineeService.createTrainee(registerTrainee);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @PathVariable(value = "username") String username
    ){
        Trainee trainee = traineeService.getTraineeByUsername(username);
        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @Valid @RequestBody UpdateTrainee updateTrainee
            ){
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
