package com.example.userservice.service.impl;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
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

    private final UserMapper userMapper;

    @Override
    public UserDto create(UserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.CONFLICT, "Username already exists");
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BusinessException(ErrorCode.CONFLICT, "Email already exists");
        });
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDto> list(Pageable pageable) {
        Page<UserDto> page = userRepository.findAll(pageable).map(userMapper::toDto);
        return PageResponse.from(page);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public UserDto update(Long id, UserRequest request) {
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
        return userMapper.toDto(userRepository.save(user));
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
