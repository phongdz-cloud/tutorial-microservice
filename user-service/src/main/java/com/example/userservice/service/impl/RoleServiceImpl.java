package com.example.userservice.service.impl;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.mapper.RoleMapper;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Override
    public RoleDto createRole(RoleRequest roleRequest) {
        roleRepository.findByName(roleRequest.getName()).ifPresent(role -> {
            log.error("[createRole] Creating role {} with name {}", role.getName(), role.getName());
            throw new BusinessException(ErrorCode.CONFLICT, "Role name already exists");
        });

        Role role = roleMapper.toEntity(roleRequest);
        role = roleRepository.save(role);

        return roleMapper.toDto(role);

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "#id")
    public RoleDto getById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(
                () -> {
                    log.error("[getById] Role with id {} not found", id);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found");
                }
        );
        return roleMapper.toDto(role);
    }

    @Override
    @CacheEvict(value = "roles", key = "#id")
    public RoleDto updateRole(Long id,RoleRequest roleRequest) {
        // check role exists
        Role role = roleRepository.findById(id).orElseThrow(
                () -> {
                    log.error("[updateRole] Role with id {} not found", id);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found");
                }
        );

        // check role name already exists in database and idRequest != idDatabase
        if(!role.getName().equals(roleRequest.getName())
                && roleRepository.findByName(roleRequest.getName()).isPresent()) {
            log.error("[updateRole] Role name already exists");
            throw new BusinessException(ErrorCode.CONFLICT, "Role name already exists");
        }

        role.setName(roleRequest.getName());
        roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    @Override
    @CacheEvict(value = "roles", key = "#id")
    public void delete(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(
                () -> {
                    log.error("[deleteRole] Role with id {} not found", id);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found");
                }
        );
        roleRepository.delete(role);
    }

    @Override
    public PageResponse<RoleDto> getAll(Pageable pageable) {
        Page<RoleDto> page = roleRepository.findAll(pageable).map(roleMapper::toDto);
        return PageResponse.from(page);
    }

}
