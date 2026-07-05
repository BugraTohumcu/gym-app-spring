package org.bugra.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private int trainingDuration;

    @Column(nullable = false)
    private String trainingName;

    @Column(nullable = false)
    private LocalDate trainingDate;

    @ManyToOne
    private TrainingType trainingType;

    @ManyToOne
    private Trainee trainee;

    @ManyToOne
    private Trainer trainer;

}