package org.bugra.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Trainer{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;


    @ManyToOne
    private TrainingType specialization;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    private User user;

    @ManyToMany(
            mappedBy = "trainers",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    private Set<Trainee> trainees;


    @OneToMany
            (
            mappedBy = "trainer",
            cascade = CascadeType.ALL
            )
    private List<Training> trainings;
}