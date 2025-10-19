package com.example.userservice.service;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.mapper.RoleMapper;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest {

    private RoleRepository roleRepository;

    private RoleMapper roleMapper;

    private RoleService roleService;

    private RoleRequest req;

    private Role testRole;

    private RoleDto testRoleDto;

    @BeforeEach
    void setUp() {
        String name = "test";
        Long roleId = 1L;

        req = new RoleRequest();
        req.setName(name);

        testRole = new Role();
        testRole.setId(roleId);
        testRole.setName(name);

        testRoleDto = new RoleDto();
        testRoleDto.setId(1L);
        testRoleDto.setName(name);


        roleRepository = Mockito.mock(RoleRepository.class);
        roleMapper = Mockito.mock(RoleMapper.class);
        roleService = new RoleServiceImpl(roleRepository, roleMapper);
    }

    @Test
    void create_conflict_name() {
        // Given
        when(roleRepository.findByName(req.getName())).thenReturn(Optional.of(new Role()));

        // When
        BusinessException ex = assertThrows(BusinessException.class, () -> roleService.createRole(req));

        // Then
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
    }

    @Test
    void createSuccess() {
        testRole.setId(null);

        // Given
        when(roleRepository.findByName(req.getName())).thenReturn(Optional.empty());
        when(roleMapper.toEntity(req)).thenReturn(testRole);
        when(roleRepository.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        // When
        var role = roleService.createRole(req);

        // Then
        assertEquals(1L, role.getId());
        assertEquals("test", role.getName());
        verify(roleRepository, times(1)).save(testRole);
    }

    @Test
    void getById() {
        // GIVEN
        Long id = 1L;

        // WHEN
        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        // Then
        RoleDto roleById = roleService.getById(id);
        assertEquals(testRoleDto.getName(), roleById.getName());
        assertEquals(testRoleDto.getId(), roleById.getId());
    }

    @Test
    void getById_notFound() {
        // GIVEN
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        BusinessException ex = assertThrows(BusinessException.class, () -> roleService.getById(id));

        // THEN
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateRoleById() {

        // GIVEN
        Long id = 1L;
        req.setName("test update");
        testRoleDto.setName("test update");
        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        // WHEN
        RoleDto roleRes = roleService.updateRole(id, req);
        assertEquals(testRoleDto.getName(), roleRes.getName());
        assertEquals(testRoleDto.getId(), roleRes.getId());


        // THEN
        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        assertEquals("test update", captor.getValue().getName());
        assertEquals(testRoleDto.getId(), captor.getValue().getId());

    }

    @Test
    void updateRoleById_sameRoleName() {
        // GIVEN
        Long id = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("test");

        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        // WHEN
        RoleDto result = roleService.updateRole(id, roleRequest);

        // THEN
        assertEquals(testRoleDto.getName(), result.getName());
        assertEquals(testRoleDto.getId(), result.getId());
    }

    @Test
    void updateRoleById_notFoundName() {
        // GIVEN
        Long id = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("SUPER_ADMIN");
        testRoleDto.setName("SUPER_ADMIN");

        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));
        when(roleRepository.findByName(roleRequest.getName())).thenReturn(Optional.empty());
        when(roleRepository.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        // WHEN
        RoleDto result = roleService.updateRole(id, roleRequest);

        // THEN
        assertEquals(testRoleDto.getName(), result.getName());
        assertEquals(testRoleDto.getId(), result.getId());
    }

    @Test
    void updateRoleById_existsRoleName() {
        // GIVEN
        Long id = 1L;
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("SUPER_ADMIN");
        testRoleDto.setName("SUPER_ADMIN");

        Role role = new Role();
        role.setId(2L);
        role.setName("SUPER_ADMIN");

        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));
        when(roleRepository.findByName(roleRequest.getName())).thenReturn(Optional.of(role));

        // WHEN
        BusinessException businessException = assertThrows(BusinessException.class, () -> roleService.updateRole(id, roleRequest));

        // THEN
        assertEquals(ErrorCode.CONFLICT, businessException.getErrorCode());
    }

    @Test
    void updateRole_NotFoundId() {
        Long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> roleService.updateRole(id, req));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteRole() {
        // GIVEN
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));

        // WHEN
        roleService.delete(id);

        // THEN
        verify(roleRepository).delete(testRole);
    }

    @Test
    void deleteRole_NotFoundId() {
        // GIVEN
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        BusinessException ex = assertThrows(BusinessException.class, () -> roleService.delete(id));

        // THEN
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void list_success() {

        // GIVEN
        Page<Role> rolePage = new PageImpl<>(List.of(testRole));

        // WHEN
        when(roleRepository.findAll(any(PageRequest.class))).thenReturn(rolePage);

        // THEN
        PageResponse<RoleDto> res = roleService.getAll(PageRequest.of(0, 10));
        assertEquals(1, res.getTotalPages());
    }

}
