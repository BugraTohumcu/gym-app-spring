package org.bugra.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        String transactionId
) {
}
