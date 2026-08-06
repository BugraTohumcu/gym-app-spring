package org.bugra.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TrainerProfileResponse(
        String firstName,
        String lastName,
        String specialization,
        boolean isActive,
        List<TraineeListSummary> trainees
) {

    public record TraineeListSummary(
            String userName,
            String firstName,
            String lastName
    ){}
}

