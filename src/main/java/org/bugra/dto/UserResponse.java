package org.bugra.dto;

import org.bugra.enums.UserRole;

public record UserResponse(
        String username,
        UserRole userRole,
        String fullName
) {
}
