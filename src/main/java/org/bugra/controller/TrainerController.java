package org.bugra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bugra.annotation.ApiNotFound;
import org.bugra.annotation.ApiValidationErrors;
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

@Tag(name = "Trainer Management", description = "Endpoints for managing trainer profiles, registration, and training creation")
@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);
    private TrainerService trainerService;
    private TrainingService trainingService;
    private TrainingResponseMapper trainingResponseMapper;
    private TrainerResponseMapper trainerResponseMapper;

    @Operation(summary = "Register a new trainer", description = "Creates a new trainer profile and returns generated credentials.")
    @ApiResponse(responseCode = "200", description = "Trainer registered successfully")
    @ApiValidationErrors
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainer(
            @Parameter(description = "Trainer registration payload", required = true)
            @Valid @RequestBody RegisterTrainer registerTrainee
    )
    {
        logger.info("New trainer is creating with name: {} {}", registerTrainee.firstName(), registerTrainee.lastName());
        Trainer trainer = trainerService.createTrainer(registerTrainee);
        UserResponse response = new UserResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get trainer profile", description = "Fetches the profile details of a trainer by username.")
    @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully")
    @ApiNotFound("Trainer not found")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable(value = "username") String username
    ){
        logger.info("The trainer profile with username {} is fetching", username);
        Trainer trainer = trainerService.getTrainerByUsername(username);
        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update trainer profile", description = "Updates an existing trainer profile.")
    @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully")
    @ApiNotFound("Trainer not found")
    @ApiValidationErrors
    @PutMapping
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @Parameter(description = "Updated trainer profile payload", required = true)
            @Valid @RequestBody UpdateTrainer updateTrainer
    ){

        logger.info("The trainer with username {} is updating profile", updateTrainer.username());
        Trainer trainer = trainerService.updateTrainer(updateTrainer);
        TrainerProfileResponse response = trainerResponseMapper.mapToTrainerProfileResponse(trainer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get trainer trainings", description = "Fetches training sessions for a trainer filtered by various criteria.")
    @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully")
    @ApiNotFound("Trainer user not found")
    @ApiValidationErrors
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainerTrainings>> getTrainerTrainings(
            @Parameter(description = "Filter parameters including trainer username, date range, trainee name, and training type")
            @Valid @ModelAttribute TrainerTrainingFilter trainerTrainingFilter
    )
    {
        logger.info("Fetching trainings for trainer with username: {}", trainerTrainingFilter.trainerUsername());
        List<Training> trainings = trainingService.getTrainerTrainings(trainerTrainingFilter);
        List<TrainerTrainings> response = trainingResponseMapper.mapToTrainerTrainings(trainings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Create a new training", description = "Creates a new training session associated with a trainer and trainee.")
    @ApiResponse(responseCode = "200", description = "Training created successfully")
    @ApiNotFound("Trainer or trainee user not found")
    @ApiValidationErrors
    @PostMapping("/trainings/training")
    public ResponseEntity<Void> createTraining(
            @Parameter(description = "Training creation payload", required = true)
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