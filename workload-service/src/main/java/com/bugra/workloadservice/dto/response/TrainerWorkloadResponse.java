package com.bugra.workloadservice.dto.response;

import com.bugra.workloadservice.model.YearlyWorkload;
import lombok.Builder;

import java.util.Map;

@Builder
public record TrainerWorkloadResponse(
        String username,
        String firstName,
        String lastName,
        boolean isActive,
        // Year -> Month, Duration
        Map<String, Map<String, Integer>> workloads
) {
}
