package com.bugra.workloadservice.service;

import com.bugra.workloadservice.dto.request.TrainerDto;

public interface TrainerService {



    /**
     * saves provided trainer
     * @param trainerDto the provided trainer dto to separate into trainer and workload models
     * */
    void saveTrainerRecord(TrainerDto trainerDto);
}
