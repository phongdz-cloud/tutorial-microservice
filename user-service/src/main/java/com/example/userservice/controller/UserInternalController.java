package com.example.userservice.controller;

import com.cursor.common.dto.UserResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    @PostMapping("/validate")
    public ResponseEntity<UserResponse> validate(@RequestBody LoginRequest loginRequest) {
        UserResponse userResponse = userService.validateUser(loginRequest);
        return ResponseEntity.ok(userResponse);
    }
}
