package com.example.userservice.service;

import com.cursor.common.dto.UserResponse;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto create(UserRequest request);

    UserDto getById(Long id);

    PageResponse<UserDto> list(Pageable pageable);

    UserDto update(Long id, UserRequest request);

    void delete(Long id);

    UserResponse validateUser(LoginRequest loginRequest);
}
