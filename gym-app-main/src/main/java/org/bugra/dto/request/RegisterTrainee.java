package org.bugra.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.bugra.shared.ValidationMessages;

import java.time.LocalDate;

@Builder
public record RegisterTrainee(

        @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.FIRST_NAME_SIZE)
        String firstName,

        @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
        @Size(min = 2, max = 50, message = ValidationMessages.LAST_NAME_SIZE)
        String lastName,

        @Past(message = ValidationMessages.DOB_PAST)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,

        @Size(min = 5, max = 100, message = ValidationMessages.ADDRESS_SIZE)
        String address
) {
}