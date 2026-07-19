package org.bugra.controller;

import jakarta.validation.Valid;
import org.bugra.dto.request.ChangePassword;
import org.bugra.dto.request.LoginUser;
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
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
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
