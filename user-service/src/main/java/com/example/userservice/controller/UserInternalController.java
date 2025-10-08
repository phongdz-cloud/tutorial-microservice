package com.example.userservice.controller;

import com.cursor.common.dto.UserResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public UserResponse validate(@RequestBody LoginRequest loginRequest) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername(loginRequest.getUsername());
        userResponse.setPassword(loginRequest.getPassword());
        return userResponse;
    }
}
