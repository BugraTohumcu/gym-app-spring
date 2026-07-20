package org.bugra.controller;


import jakarta.validation.Valid;
import org.bugra.dto.request.CreateTraining;
import org.bugra.dto.request.RegisterTrainer;
import org.bugra.dto.request.TrainerTrainingFilter;
import org.bugra.dto.request.UpdateTrainer;
import org.bugra.dto.response.TrainerProfileResponse;
import org.bugra.dto.response.TrainerTrainings;
import org.bugra.dto.response.UserResponse;
import org.bugra.mapper.TrainerResponseMapper;
import org.bugra.mapper.TrainingResponseMapper;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.service.TrainerService;
import org.bugra.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);
    private TrainerService trainerService;
    private TrainingService trainingService;
    private TrainingResponseMapper trainingResponseMapper;
    private TrainerResponseMapper trainerResponseMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainer(
            @Valid @RequestBody RegisterTrainer registerTrainee
    )
    {
        logger.info("New trainer is creating with name: {} {}", registerTrainee.firstName(), registerTrainee.lastName());
        Trainer trainer = trainerService.createTrainer(registerTrainee);
        UserResponse response = new UserResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @PathVariable(value = "username") String username
    ){
        logger.info("The trainer profile with username {} is fetching", username);
        Trainer trainer = trainerService.getTrainerByUsername(username);
        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @Valid @RequestBody UpdateTrainer updateTrainer
    ){

        logger.info("The trainer with username {} is updating profile", updateTrainer.username());
        Trainer trainer = trainerService.updateTrainer(updateTrainer);
        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/trainings")
    public ResponseEntity<List<TrainerTrainings>> getTrainerTrainings(
            @Valid @ModelAttribute TrainerTrainingFilter trainerTrainingFilter
    )
    {
        logger.info("Fetching trainings for trainer with username: {}", trainerTrainingFilter.trainerUsername());
        List<Training> trainings = trainingService.getTrainerTrainings(trainerTrainingFilter);
        List<TrainerTrainings> response = trainingResponseMapper.mapToTrainerTrainings(trainings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/trainings/training")
    public ResponseEntity<Void> createTraining(
            @Valid @RequestBody CreateTraining createTraining
            )
    {
        logger.info("The trainer: {} is creating new training", createTraining.trainerUsername());
        trainingService.createTraining(createTraining);
        return ResponseEntity.ok().build();
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
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setTrainerResponseMapper(TrainerResponseMapper trainerResponseMapper) {
        this.trainerResponseMapper = trainerResponseMapper;
    }
}
