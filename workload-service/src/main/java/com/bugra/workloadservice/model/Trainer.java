package com.bugra.workloadservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;


@Data
@Document(collection = "trainers")
public class Trainer {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String username;

    private boolean isActive;

    private Map<String, Map<String, Integer>> workloads = new HashMap<>();
}
