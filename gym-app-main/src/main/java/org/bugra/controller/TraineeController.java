package org.bugra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bugra.annotation.ApiNotFound;
import org.bugra.annotation.ApiValidationErrors;
import org.bugra.dto.request.RegisterTrainee;
import org.bugra.dto.request.TraineeTrainingFilter;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Trainee Management", description = "Endpoints for managing trainee profiles, registration, and training associations")
@RestController
@RequestMapping(value = "/trainee")
@RequiredArgsConstructor
public class TraineeController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeController.class);
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TraineeResponseMapper traineeResponseMapper;
    private final TrainingService trainingService;
    private final TrainingResponseMapper trainingResponseMapper;

    @Operation(summary = "Register a new trainee", description = "Creates a new trainee profile and returns generated credentials.")
    @ApiResponse(responseCode = "200", description = "Trainee registered successfully")
    @ApiValidationErrors
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainee(
            @Parameter(description = "Trainee registration payload", required = true)
            @Valid @RequestBody RegisterTrainee registerTrainee
    ) {
        logger.info("New trainee is creating with name: {} {}", registerTrainee.firstName(), registerTrainee.lastName());
        UserResponse response = traineeService.createTrainee(registerTrainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get trainee profile", description = "Fetches the trainee profile by username.")
    @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully")
    @ApiNotFound("Trainee user not found")
    @GetMapping("/{username}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable(value = "username") String username
    ) {
        logger.info("The trainee profile with username {} is fetching", username);
        Trainee trainee = traineeService.getTraineeByUsername(username);
        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update trainee profile", description = "Updates an existing trainee profile.")
    @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully")
    @ApiNotFound("Trainee user not found")
    @ApiValidationErrors
    @PutMapping
    @PreAuthorize("#updateTrainee.username() == authentication.name")
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @Parameter(description = "Updated trainee profile payload", required = true)
            @Valid @RequestBody UpdateTrainee updateTrainee
    ) {
        logger.info("The trainee with username {} is updating profile", updateTrainee.username());
        Trainee trainee = traineeService.updateTrainee(updateTrainee);
        TraineeProfileResponse response = traineeResponseMapper.mapToTraineeProfileResponse(trainee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete trainee profile", description = "Deletes a trainee profile by username.")
    @ApiResponse(responseCode = "204", description = "Trainee profile deleted successfully", content = @Content)
    @ApiNotFound("Trainee user not found")
    @DeleteMapping("/{username}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "Username of the trainee to delete", required = true)
            @PathVariable(value = "username") String username
    ) {
        logger.info("The trainee with username {} is deleting profile", username);
        traineeService.deleteTraineeByUsername(username);

        logger.info("The trainee with username {} deleted profile", username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get unassigned trainers", description = "Fetches a list of trainers who are not currently assigned to the given trainee.")
    @ApiResponse(responseCode = "200", description = "List of unassigned trainers retrieved successfully")
    @ApiNotFound("Trainee user not found")
    @GetMapping("/not-assigned")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<List<TraineeProfileResponse.TrainerSummary>> getAvailableTrainers(
            @Parameter(description = "Username of the trainee", required = true)
            @RequestParam(value = "username") String username
    ) {
        logger.info("Fetching trainers not assigned to trainee with username {}", username);
        List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(username);
        List<TraineeProfileResponse.TrainerSummary> response = traineeResponseMapper.mapToTrainerSummary(trainers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update trainee's trainers list", description = "Replaces the assigned trainers list for a given trainee.")
    @ApiResponse(responseCode = "200", description = "Trainer list updated successfully")
    @ApiNotFound("Trainee or trainer user not found")
    @ApiValidationErrors
    @PutMapping("/trainers")
    @PreAuthorize("#updateTrainersList.username() == authentication.name")
    public ResponseEntity<List<TraineeProfileResponse.TrainerSummary>> updateTrainers(
            @Parameter(description = "Payload containing trainee username and updated list of trainer usernames", required = true)
            @Valid @RequestBody UpdateTrainersList updateTrainersList
    ) {
        logger.info("Updating trainee's trainer list for trainee with the username {}", updateTrainersList.username());
        Trainee trainee = traineeService.updateTraineeTrainers(updateTrainersList.username(), updateTrainersList.trainerUsernames());
        Set<Trainer> trainers = trainee.getTrainers();
        List<TraineeProfileResponse.TrainerSummary> response = traineeResponseMapper.mapToTrainerSummary(trainers.stream().toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get trainee trainings", description = "Fetches training sessions for a trainee filtered by various criteria.")
    @ApiResponse(responseCode = "200", description = "List of trainings retrieved successfully")
    @ApiNotFound("Trainee user or training type not found")
    @ApiValidationErrors
    @GetMapping("/trainings")
    @PreAuthorize("#traineeTrainingFilter.traineeUsername() == authentication.name")
    public ResponseEntity<List<TraineeTrainings>> getTraineeTrainings(
            @Parameter(description = "Filter parameters including trainee username, date range, trainer name, and training type")
            @Valid @ModelAttribute TraineeTrainingFilter traineeTrainingFilter
    ) {
        logger.info("Fetching trainings for trainee with username: {}", traineeTrainingFilter.traineeUsername());
        List<Training> trainings = trainingService.getTraineeTrainings(traineeTrainingFilter);
        List<TraineeTrainings> response = trainingResponseMapper.mapToTraineeTrainings(trainings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}