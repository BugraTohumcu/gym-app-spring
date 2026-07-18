package org.bugra.dto.request;

public record ChangePassword(
        String currentPassword,
        String newPassword
) {
}
