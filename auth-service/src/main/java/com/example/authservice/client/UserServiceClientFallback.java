package com.example.authservice.client;

import com.cursor.common.dto.UserResponse;
import com.example.authservice.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements  UserServiceClient {
    @Override
    public UserResponse validateUser(LoginRequest loginRequest) {
        UserResponse unauthenticated = new UserResponse();
        unauthenticated.setStatus(HttpStatus.UNAUTHORIZED.value());
        return unauthenticated;
    }
}
