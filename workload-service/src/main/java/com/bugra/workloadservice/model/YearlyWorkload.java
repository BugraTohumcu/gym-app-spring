package com.bugra.workloadservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class YearlyWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "workload_year", nullable = false)
    private String year;

    @ManyToOne
    private Trainer trainer;

    @ElementCollection
    @CollectionTable(
            name = "monthly_workload",
            joinColumns = @JoinColumn(name = "yearly_workload_id")
    )

    @MapKeyColumn(name = "workload_month")
    private Map<String, MonthlyWorkload> months = new HashMap<>();
}