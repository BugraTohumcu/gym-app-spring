package org.bugra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private TrainingType trainingType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Trainee trainee;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Trainer trainer;

}