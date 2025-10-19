//package com.example.userservice.integration;
//
//import com.cursor.common.exception.BusinessException;
//import com.cursor.common.exception.ErrorCode;
//import com.cursor.common.pagination.PageResponse;
//import com.example.userservice.dto.RoleDto;
//import com.example.userservice.dto.RoleRequest;
//import com.example.userservice.entity.Role;
//import com.example.userservice.repository.RoleRepository;
//import com.example.userservice.service.RoleService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cache.CacheManager;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//class RoleServiceIntegrationTest {
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private CacheManager cacheManager;
//
//    @BeforeEach
//    void setUp() {
//        roleRepository.deleteAll();
//        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
//    }
//
//    @Test
//    void createRole_ShouldCreateAndReturnRole() {
//        // Given
//        RoleRequest request = new RoleRequest("ADMIN");
//
//        // When
//        RoleDto result = roleService.createRole(request);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getName()).isEqualTo("ADMIN");
//        assertThat(result.getId()).isNotNull();
//
//        // Verify in database
//        List<Role> roles = roleRepository.findAll();
//        assertThat(roles).hasSize(1);
//        assertThat(roles.get(0).getName()).isEqualTo("ADMIN");
//    }
//
//    @Test
//    void createRole_WithDuplicateName_ShouldThrowException() {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("ADMIN");
//
//        // When & Then
//        assertThatThrownBy(() -> roleService.createRole(request))
//                .isInstanceOf(BusinessException.class)
//                .hasMessage("Role name already exists")
//                .satisfies(exception -> {
//                    BusinessException businessException = (BusinessException) exception;
//                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.CONFLICT);
//                });
//    }
//
//    @Test
//    void getById_ShouldReturnRole() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When
//        RoleDto result = roleService.getById(role.getId());
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(role.getId());
//        assertThat(result.getName()).isEqualTo("ADMIN");
//    }
//
//    @Test
//    void getById_WithNonExistentId_ShouldThrowException() {
//        // When & Then
//        assertThatThrownBy(() -> roleService.getById(999L))
//                .isInstanceOf(BusinessException.class)
//                .hasMessage("Role not found")
//                .satisfies(exception -> {
//                    BusinessException businessException = (BusinessException) exception;
//                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
//                });
//    }
//
//    @Test
//    void updateRole_ShouldUpdateAndReturnRole() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When
//        RoleDto result = roleService.updateRole(role.getId(), request);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(role.getId());
//        assertThat(result.getName()).isEqualTo("SUPER_ADMIN");
//
//        // Verify in database
//        Role updatedRole = roleRepository.findById(role.getId()).orElseThrow();
//        assertThat(updatedRole.getName()).isEqualTo("SUPER_ADMIN");
//    }
//
//    @Test
//    void updateRole_WithNonExistentId_ShouldThrowException() {
//        // Given
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When & Then
//        assertThatThrownBy(() -> roleService.updateRole(999L, request))
//                .isInstanceOf(BusinessException.class)
//                .hasMessage("Role not found")
//                .satisfies(exception -> {
//                    BusinessException businessException = (BusinessException) exception;
//                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
//                });
//    }
//
//    @Test
//    void delete_ShouldDeleteRole() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When
//        roleService.delete(role.getId());
//
//        // Then
//        assertThat(roleRepository.findById(role.getId())).isEmpty();
//    }
//
//    @Test
//    void delete_WithNonExistentId_ShouldThrowException() {
//        // When & Then
//        assertThatThrownBy(() -> roleService.delete(999L))
//                .isInstanceOf(BusinessException.class)
//                .hasMessage("Role not found")
//                .satisfies(exception -> {
//                    BusinessException businessException = (BusinessException) exception;
//                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
//                });
//    }
//
//    @Test
//    void getAll_WithDefaultPagination_ShouldReturnAllRoles() {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        roleRepository.save(new Role("USER"));
//        roleRepository.save(new Role("GUEST"));
//
//        // When
//        PageResponse<RoleDto> result = roleService.getAll(PageRequest.of(0, 20));
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(3);
//        assertThat(result.getTotalElements()).isEqualTo(3);
//        assertThat(result.getPageNumber()).isEqualTo(0);
//        assertThat(result.getPageSize()).isEqualTo(20);
//        assertThat(result.isFirst()).isTrue();
//        assertThat(result.isLast()).isTrue();
//    }
//
//    @Test
//    void getAll_WithCustomPagination_ShouldReturnPaginatedResults() {
//        // Given
//        for (int i = 1; i <= 5; i++) {
//            roleRepository.save(new Role("ROLE_" + i));
//        }
//
//        // When
//        PageResponse<RoleDto> result = roleService.getAll(PageRequest.of(0, 2));
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(2);
//        assertThat(result.getTotalElements()).isEqualTo(5);
//        assertThat(result.getPageNumber()).isEqualTo(0);
//        assertThat(result.getPageSize()).isEqualTo(2);
//        assertThat(result.isFirst()).isTrue();
//        assertThat(result.isLast()).isFalse();
//    }
//
//    @Test
//    void getAll_WithSorting_ShouldReturnSortedResults() {
//        // Given
//        roleRepository.save(new Role("Z_ROLE"));
//        roleRepository.save(new Role("A_ROLE"));
//        roleRepository.save(new Role("M_ROLE"));
//
//        // When
//        Pageable pageable = PageRequest.of(0, 10);
//        PageResponse<RoleDto> result = roleService.getAll(pageable);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(3);
//
//        // Verify the order (should be sorted by name by default)
//        List<RoleDto> content = result.getContent();
//        assertThat(content.get(0).getName()).isEqualTo("A_ROLE");
//        assertThat(content.get(1).getName()).isEqualTo("M_ROLE");
//        assertThat(content.get(2).getName()).isEqualTo("Z_ROLE");
//    }
//
//    @Test
//    void getAll_WithEmptyDatabase_ShouldReturnEmptyPage() {
//        // When
//        PageResponse<RoleDto> result = roleService.getAll(PageRequest.of(0, 10));
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).isEmpty();
//        assertThat(result.getTotalElements()).isEqualTo(0);
//        assertThat(result.getPageNumber()).isEqualTo(0);
//        assertThat(result.getPageSize()).isEqualTo(10);
//        assertThat(result.isFirst()).isTrue();
//        assertThat(result.isLast()).isTrue();
//    }
//
//    @Test
//    void getById_ShouldCacheResult() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When - First call
//        RoleDto result1 = roleService.getById(role.getId());
//
//        // When - Second call (should use cache)
//        RoleDto result2 = roleService.getById(role.getId());
//
//        // Then
//        assertThat(result1).isNotNull();
//        assertThat(result2).isNotNull();
//        assertThat(result1.getId()).isEqualTo(result2.getId());
//        assertThat(result1.getName()).isEqualTo(result2.getName());
//    }
//
//    @Test
//    void updateRole_ShouldEvictCache() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When
//        RoleDto result = roleService.updateRole(role.getId(), request);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getName()).isEqualTo("SUPER_ADMIN");
//
//        // Verify in database
//        Role updatedRole = roleRepository.findById(role.getId()).orElseThrow();
//        assertThat(updatedRole.getName()).isEqualTo("SUPER_ADMIN");
//    }
//
//    @Test
//    void delete_ShouldEvictCache() {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When
//        roleService.delete(role.getId());
//
//        // Then
//        assertThat(roleRepository.findById(role.getId())).isEmpty();
//    }
//
//    @Test
//    void createRole_ShouldEvictAllCache() {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("USER");
//
//        // When
//        RoleDto result = roleService.createRole(request);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getName()).isEqualTo("USER");
//
//        // Verify in database
//        List<Role> roles = roleRepository.findAll();
//        assertThat(roles).hasSize(2);
//    }
//
//    @Test
//    void getAll_WithLargeDataset_ShouldHandlePaginationCorrectly() {
//        // Given
//        for (int i = 1; i <= 25; i++) {
//            roleRepository.save(new Role("ROLE_" + String.format("%02d", i)));
//        }
//
//        // When - First page
//        PageResponse<RoleDto> firstPage = roleService.getAll(PageRequest.of(0, 10));
//
//        // Then
//        assertThat(firstPage.getContent()).hasSize(10);
//        assertThat(firstPage.getTotalElements()).isEqualTo(25);
//        assertThat(firstPage.getPageNumber()).isEqualTo(0);
//        assertThat(firstPage.getPageSize()).isEqualTo(10);
//        assertThat(firstPage.isFirst()).isTrue();
//        assertThat(firstPage.isLast()).isFalse();
//
//        // When - Last page
//        PageResponse<RoleDto> lastPage = roleService.getAll(PageRequest.of(2, 10));
//
//        // Then
//        assertThat(lastPage.getContent()).hasSize(5);
//        assertThat(lastPage.getTotalElements()).isEqualTo(25);
//        assertThat(lastPage.getPageNumber()).isEqualTo(2);
//        assertThat(lastPage.getPageSize()).isEqualTo(10);
//        assertThat(lastPage.isFirst()).isFalse();
//        assertThat(lastPage.isLast()).isTrue();
//    }
//}
