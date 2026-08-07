package com.bugra.workloadservice.controller;


import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.ErrorResponse;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;
import com.bugra.workloadservice.service.TrainerService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @ApiResponse(responseCode = "200", description = "Create new trainer workload record")
    @ApiResponse(responseCode = "422", description = "Invalid request input",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> createTrainerRecord
            (
            @Valid @RequestBody TrainerDto trainerDto
            )
    {
        trainerService.saveTrainerRecord(trainerDto);
        return ResponseEntity.ok().build();
    }

    @ApiResponse(responseCode = "200", description = "Get trainer workload")
    @GetMapping("/workload")
    public ResponseEntity<TrainerWorkloadResponse> getWorkload
            (
                    @RequestParam("username") String username
            )
    {
        TrainerWorkloadResponse workloads = trainerService.getTrainerWorkload(username);
        return ResponseEntity.ok().body(workloads);
    }
}
