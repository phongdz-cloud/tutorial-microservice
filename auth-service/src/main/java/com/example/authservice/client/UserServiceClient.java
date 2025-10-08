package com.example.authservice.client;

import com.example.authservice.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserServiceClient {

    @PostMapping("/validate")
    Object validateUser(@RequestBody LoginRequest userRequest);
}
