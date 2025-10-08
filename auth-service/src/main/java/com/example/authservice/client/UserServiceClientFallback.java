package com.example.authservice.client;

import com.cursor.common.dto.UserResponse;
import com.example.authservice.dto.LoginRequest;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements  UserServiceClient {
    @Override
    public UserResponse validateUser(LoginRequest loginRequest) {
        return new UserResponse(
                0L,
                loginRequest.getUsername(),
                "unknow",
                "unknow"
        );
    }
}
