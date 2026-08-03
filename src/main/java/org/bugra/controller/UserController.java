package org.bugra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bugra.annotation.ApiNotFound;
import org.bugra.annotation.ApiValidationErrors;
import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "Endpoints for managing user accounts and status updates")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Toggle user active status", description = "Updates the active or inactive status of a user by username.")
    @ApiResponse(responseCode = "200", description = "User status updated successfully")
    @ApiNotFound("User not found")
    @ApiValidationErrors
    @PatchMapping("/{username}/status")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<Void> toggleActiveStatus(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable(value = "username") String username,
            @Parameter(description = "Payload containing updated user status details", required = true)
            @Valid @RequestBody UpdateUserStatus userStatus
    )
    {

        userService.toggleActiveStatus(username, userStatus);
        return ResponseEntity.ok().build();
    }
}