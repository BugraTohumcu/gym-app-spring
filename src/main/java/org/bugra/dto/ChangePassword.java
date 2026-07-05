package org.bugra.dto;

public record ChangePassword(
        String currentPassword,
        String newPassword
) {
}
