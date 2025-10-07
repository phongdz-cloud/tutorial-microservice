package com.example.userservice.mapper;

import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequest roleRequest);

    RoleDto toDto(Role role);
}
