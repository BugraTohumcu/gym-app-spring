package org.bugra.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Trainee{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private LocalDate dateOfBirth;
    private String address;

    @OneToOne(
            optional = false,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private User user;


    @ManyToMany(mappedBy = "trainees")
    private Set<Trainer> trainers;
}