package com.bugra.workloadservice.dto.response;

import com.bugra.workloadservice.model.YearlyWorkload;

import java.util.List;

public record TrainerWorkloadResponse(
        String username,
        String firstName,
        String lastName,
        boolean isActive,
        List<YearlyWorkload> workloads
) {
}
