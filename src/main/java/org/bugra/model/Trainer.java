package org.bugra.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
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

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private Set<Trainee> trainees;
}