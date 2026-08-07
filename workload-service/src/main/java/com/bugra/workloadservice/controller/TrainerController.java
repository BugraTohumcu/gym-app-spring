package com.bugra.workloadservice.controller;


import com.bugra.workloadservice.dto.TrainerDto;
import com.bugra.workloadservice.service.TrainerService;
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
