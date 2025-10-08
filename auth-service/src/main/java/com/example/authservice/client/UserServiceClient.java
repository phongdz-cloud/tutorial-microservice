package com.example.authservice.client;

import com.cursor.common.dto.UserResponse;
import com.example.authservice.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service",
        path = "/internal/users",
        fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @PostMapping("/validate")
    UserResponse validateUser(@RequestBody LoginRequest userRequest);

}
