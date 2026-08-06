package com.bugra.workloadservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Entity
@Getter
@Setter
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @OneToMany(
            cascade = {CascadeType.ALL},
            mappedBy = "trainer"
    )
    @MapKey(name = "year")
    private Map<String, YearlyWorkload> workloads = new HashMap<>();

    public void addWorkload(YearlyWorkload yearlyWorkload){
        workloads.put(yearlyWorkload.getYear(), yearlyWorkload);
        yearlyWorkload.setTrainer(this);
    }
}
