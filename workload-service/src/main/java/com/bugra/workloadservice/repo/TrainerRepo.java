package com.bugra.workloadservice.repo;

import com.bugra.workloadservice.model.Trainer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepo extends JpaRepository<Trainer, Long> {

    @EntityGraph(attributePaths = {"workloads", "workloads.months"})
    Optional<Trainer> findByUsername(String username);
}
