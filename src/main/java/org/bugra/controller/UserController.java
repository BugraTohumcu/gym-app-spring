package org.bugra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bugra.dto.request.UpdateUserStatus;
import org.bugra.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> toggleActiveStatus(
            @PathVariable(value = "username") String username,
            @Valid @RequestBody UpdateUserStatus userStatus
            )
    {

        userService.toggleActiveStatus(username, userStatus);
        return ResponseEntity.ok().build();
    }
}
