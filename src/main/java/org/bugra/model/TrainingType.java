package org.bugra.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private String trainingTypeName;

}
