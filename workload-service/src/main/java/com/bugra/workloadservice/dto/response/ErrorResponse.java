package com.bugra.workloadservice.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message
) {
}