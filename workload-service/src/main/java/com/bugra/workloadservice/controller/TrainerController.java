package com.bugra.workloadservice.controller;


import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.service.TrainerService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @ApiResponse(responseCode = "200", description = "Create new trainer workload record")
    @ApiResponse(responseCode = "422", description = "Invalid request input")
    @PostMapping
    public ResponseEntity<Void> createTrainerRecord
            (
            @Valid @RequestBody TrainerDto trainerDto
            )
    {
        trainerService.saveTrainerRecord(trainerDto);
        return ResponseEntity.ok().build();
    }

}
