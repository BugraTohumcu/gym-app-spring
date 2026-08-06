package com.bugra.workloadservice.repo;

import com.bugra.workloadservice.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepo extends JpaRepository<Trainer, Long> {
}
