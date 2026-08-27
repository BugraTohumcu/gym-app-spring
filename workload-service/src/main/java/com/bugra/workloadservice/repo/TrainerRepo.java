package com.bugra.workloadservice.repo;

import com.bugra.workloadservice.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface TrainerRepo extends MongoRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);
}
