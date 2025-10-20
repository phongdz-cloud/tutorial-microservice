package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.mapper.RoleMapper;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.service.impl.RoleServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

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
    void create_success() {
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
    void getById_success() {
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
    void updateRole_success() {

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
    void updateRole_sameRoleName_success() {
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
    void updateRole_uniqueName_success() {
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
    void updateRole_existingName_conflict() {
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
    void updateRole_notFoundId() {
        Long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> roleService.updateRole(id, req));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteRole_success() {
        // GIVEN
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.of(testRole));

        // WHEN
        roleService.delete(id);

        // THEN
        verify(roleRepository).delete(testRole);
    }

    @Test
    void deleteRole_notFoundId() {
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
