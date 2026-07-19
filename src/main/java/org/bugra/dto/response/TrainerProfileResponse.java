package org.bugra.dto.response;

import java.util.List;

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

