package com.example.userservice.service.impl;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse create(UserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.CONFLICT, "Username already exists");
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.CONFLICT, "Email already exists");
        });
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
        if (!user.getUsername().equals(request.getUsername())) {
            userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
                throw new BusinessException(ErrorCode.CONFLICT, "Username already exists");
            });
        }
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
                throw new BusinessException(ErrorCode.CONFLICT, "Email already exists");
            });
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }
}
