package org.bugra.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterTrainee(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name should be between 2 - 50 chars")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name should be between 2 - 50 chars")
        String lastName,

        @Past(message = "Date of birth should be in past date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,

        @Size(max = 100, message ="Address length can not pass 100 chars" )
        String address
) {
}