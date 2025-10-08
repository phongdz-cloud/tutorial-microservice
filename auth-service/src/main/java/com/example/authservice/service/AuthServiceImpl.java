package com.example.authservice.service;

import com.cursor.common.jwt.JwtService;
import com.example.authservice.client.UserServiceClient;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;

    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Object test = userServiceClient.validateUser(loginRequest);
        return null;
    }
}
