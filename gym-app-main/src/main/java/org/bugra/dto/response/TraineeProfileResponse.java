package org.bugra.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TraineeProfileResponse(
        String username,
        String firstName,
        String lastName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,

        String address,
        boolean isActive,

        List<TrainerSummary> trainers
) {
    public record TrainerSummary(
            String username,
            String firstName,
            String lastName,
            String specialization
    ) {}
}