package com.example.userservice.service;

import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleDto createRole(RoleRequest roleRequest);

    RoleDto getById(Long id);

    RoleDto updateRole(Long id, RoleRequest roleRequest);

    void delete(Long id);

    PageResponse<RoleDto> getAll(Pageable pageable);
}
