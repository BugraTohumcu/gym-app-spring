package org.bugra.controller;


import jakarta.validation.Valid;
import org.bugra.dto.request.TraineeTrainingFilter;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.UpdateTrainee;
import org.bugra.dto.request.UpdateTrainersList;
import org.bugra.dto.response.TraineeProfileResponse;
import org.bugra.dto.response.TraineeTrainings;
import org.bugra.dto.response.UserResponse;
import org.bugra.mapper.TraineeResponseMapper;
import org.bugra.mapper.TrainingResponseMapper;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.service.TraineeService;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/trainee")
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TraineeResponseMapper traineeResponseMapper;
    private TrainingService trainingService;
    private TrainingResponseMapper trainingResponseMapper;

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

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @PathVariable(value = "username") String username
    ) {
        logger.info("The trainee with username {} is deleting profile", username);
        traineeService.deleteTraineeByUsername(username);

        logger.info("The trainee with username {} deleted profile", username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/not-assigned")
    public ResponseEntity<List<TraineeProfileResponse.TrainerSummary>> getAvailableTrainers(
            @RequestParam(value = "username") String username
    ){
        logger.info("Fetching trainers not assigned to trainee with username {}", username);
        List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(username);

        List<TraineeProfileResponse.TrainerSummary> response = traineeResponseMapper.mapToTrainerSummary(trainers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/trainers")
    public ResponseEntity<List<TraineeProfileResponse.TrainerSummary>> updateTrainers(
            @Valid @RequestBody UpdateTrainersList updateTrainersList
            ){

        logger.info("Updating trainee's trainer list for trainee with the username {}", updateTrainersList.username());
        Trainee trainee = traineeService.updateTraineeTrainers(updateTrainersList.username(), updateTrainersList.trainerUsernames());
        Set<Trainer> trainers = trainee.getTrainers();
        List<TraineeProfileResponse.TrainerSummary> response = traineeResponseMapper.mapToTrainerSummary(trainers.stream().toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<TraineeTrainings>> getTraineeTrainings(
            @Valid @ModelAttribute TraineeTrainingFilter traineeTrainingFilter
            )
    {
        logger.info("Fetching trainings for trainee with username: {}", traineeTrainingFilter.traineeUsername());
        List<Training> trainings = trainingService.getTraineeTrainings(traineeTrainingFilter);
        List<TraineeTrainings> response = trainingResponseMapper.mapToTraineeTrainings(trainings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Autowired
    public void setTrainingResponseMapper(TrainingResponseMapper trainingResponseMapper) {
        this.trainingResponseMapper = trainingResponseMapper;
    }

    @Autowired
    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setTraineeResponseMapper(TraineeResponseMapper traineeResponseMapper) {
        this.traineeResponseMapper = traineeResponseMapper;
    }
}
