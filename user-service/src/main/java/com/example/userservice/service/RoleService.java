package com.example.userservice.service;

import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;

public interface RoleService {

    RoleDto createRole(RoleRequest roleRequest);

    RoleDto getById(Long id);

    RoleDto updateRole(Long id, RoleRequest roleRequest);

    void delete(Long id);
}
