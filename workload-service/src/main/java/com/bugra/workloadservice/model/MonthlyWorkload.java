package com.bugra.workloadservice.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Table(name = "monthly_workload")
public class MonthlyWorkload {
    private int duration;
}
