package org.bugra.controller;


import lombok.AllArgsConstructor;
import org.bugra.model.TrainingType;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainingTypes")
@AllArgsConstructor
public class TrainingTypeController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeController.class);
    private TrainingTypeRepo trainingTypeRepo;

    @GetMapping
    public ResponseEntity<List<TrainingType>> getTrainingTypes(){
        logger.info("Fetching all training types");
        List<TrainingType> response = trainingTypeRepo.findAllTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
