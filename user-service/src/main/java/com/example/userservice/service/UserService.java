package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    Page<UserResponse> list(Pageable pageable);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
