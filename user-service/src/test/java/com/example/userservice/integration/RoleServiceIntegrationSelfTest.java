package com.example.userservice.integration;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleServiceIntegrationSelfTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        roleRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        });
    }

    @Test
    void createRole_ShouldCreateAndReturnRole() {
        // Given
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("ADMIN");

        // When
        RoleDto result = roleService.createRole(roleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ADMIN");
        assertThat(result.getId()).isNotNull();

        // Verify in database
        List<Role> roles = roleRepository.findAll();
        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getName()).isEqualTo("ADMIN");
    }

    @Test
    void createRole_WithDuplicateName_ShouldThrowException() {
        // GIVEN
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("ADMIN");

        // When & Then
        assertThatThrownBy(() -> roleService.createRole(roleRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role name already exists")
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                });
    }

    @Test
    void getById_ShouldReturnRole() {
        // GIVEN
        Role role = new Role();
        role.setName("ADMIN");
        Role saved = roleRepository.save(role);

        // When
        RoleDto roleDto = roleService.getById(saved.getId());

        // Then
        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getId()).isEqualTo(saved.getId());
        assertThat(roleDto.getName()).isEqualTo("ADMIN");
    }

    @Test
    void getById_WithNonExistentId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> roleService.getById(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    @Test
    void updateRole_ShouldUpdateAndReturnRole() {
        // GIVEN
        Role role = new Role();
        role.setName("ADMIN");
        Role saved = roleRepository.save(role);
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("SUPER_ADMIN");

        // WHEN
        RoleDto result = roleService.updateRole(saved.getId(), roleRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getName()).isEqualTo("SUPER_ADMIN");

        // Verify in database
        Role updateRole = roleRepository.findById(result.getId()).orElseThrow();
        assertThat(updateRole.getName()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    void updateRole_WithNonExistentId_ShouldThrowException() {
        // Given
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("SUPER_ADMIN");

        // When & Then
        assertThatThrownBy(() -> roleService.updateRole(999L, roleRequest))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    @Test
    void updateRole_ExistRoleName_ShouldThrowException() {
        // GIVEN
        Role role = new Role();
        role.setName("ADMIN");
        Role saved = roleRepository.save(role);

        Role roleSuperAdmin = new Role();
        roleSuperAdmin.setName("SUPER_ADMIN");
        roleRepository.save(roleSuperAdmin);

        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("SUPER_ADMIN");

        // WHEN & THEN
        assertThatThrownBy(() -> roleService.updateRole(saved.getId(), roleRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role name already exists")
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
                });

    }

    @Test
    void deleteRole_ShouldDeleteRole() {
        // GIVEN
        Role role = new Role();
        role.setName("ADMIN");
        Role saved = roleRepository.save(role);

        // WHEN
        roleRepository.delete(saved);

        // THEN
        assertThat(roleRepository.findById(saved.getId())).isEmpty();

    }

    @Test
    void deleteRole_WithNonExistentId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> roleService.delete(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }



}
