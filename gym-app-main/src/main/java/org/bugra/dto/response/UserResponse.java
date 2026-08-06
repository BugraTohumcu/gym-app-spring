package org.bugra.dto.response;


public record UserResponse(
        String username,
        String password,
        String accessToken) {
}
