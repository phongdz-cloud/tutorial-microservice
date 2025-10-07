package com.example.userservice.dto;

import com.example.userservice.entity.User;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private User.Status status;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setStatus(user.getStatus());
        r.setCreatedAt(user.getCreatedAt());
        r.setUpdatedAt(user.getUpdatedAt());
        return r;
    }

}
