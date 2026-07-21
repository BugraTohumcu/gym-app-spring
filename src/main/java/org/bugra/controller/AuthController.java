package org.bugra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.bugra.annotation.ApiValidationErrors;
import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
import org.bugra.dto.response.ErrorResponse;
import org.bugra.dto.response.UserResponse;
import org.bugra.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@Tag(name = "Auth Management", description = "Endpoints for managing logging and password update")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login user with provided username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logins user and returns username and password"),
            @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ApiValidationErrors
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginUser loginUser
            ){

        UserResponse userResponse = authService.login(loginUser);
        String token = Base64.getEncoder().encodeToString(
                (loginUser.username() + ":" + loginUser.password()).getBytes()
        );
        return ResponseEntity.ok()
                .header("Authorization", token)
                .body(userResponse);
    }

    @PutMapping("/login")
    @Operation(summary = "Update Password", description = "Update with provided username , current password and new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updates user password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Invalid Password")
    })
    @ApiValidationErrors
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePassword changePassword
            )
    {
       authService.changePassword(changePassword);
        String token = Base64.getEncoder().encodeToString(
                (changePassword.username() + ":" + changePassword.newPassword()).getBytes()
        );

        return ResponseEntity.ok()
                .header("Authorization", token)
                .build();
    }


    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
