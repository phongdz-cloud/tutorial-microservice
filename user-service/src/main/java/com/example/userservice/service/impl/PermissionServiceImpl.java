package com.example.userservice.service.impl;

import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
}
