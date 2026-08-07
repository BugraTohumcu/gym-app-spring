package com.bugra.workloadservice.service;

import com.bugra.workloadservice.dto.request.TrainerDto;
import com.bugra.workloadservice.dto.response.TrainerWorkloadResponse;

public interface TrainerService {



    /**
     * Saves provided trainer
     * @param trainerDto the provided trainer dto to separate into trainer and workload models
     * */
    void saveTrainerRecord(TrainerDto trainerDto);


    /**
     * Retrieves all workload of the trainer
     * @param username trainer username
     * @return {@link TrainerWorkloadResponse} dto
     */
    TrainerWorkloadResponse getTrainerWorkload(String username);
}
