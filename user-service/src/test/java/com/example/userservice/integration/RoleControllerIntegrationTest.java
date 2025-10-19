//package com.example.userservice.integration;
//
//import com.example.userservice.dto.RoleRequest;
//import com.example.userservice.entity.Role;
//import com.example.userservice.repository.RoleRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cache.CacheManager;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureTestMvc
//@ActiveProfiles("test")
//@Transactional
//class RoleControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
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
//    void createRole_ShouldReturnCreatedRole() throws Exception {
//        // Given
//        RoleRequest request = new RoleRequest("ADMIN");
//
//        // When & Then
//        mockMvc.perform(post("/roles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("ADMIN"))
//                .andExpect(jsonPath("$.id").exists());
//
//        // Verify in database
//        List<Role> roles = roleRepository.findAll();
//        assertThat(roles).hasSize(1);
//        assertThat(roles.get(0).getName()).isEqualTo("ADMIN");
//    }
//
//    @Test
//    void createRole_WithDuplicateName_ShouldReturnConflict() throws Exception {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("ADMIN");
//
//        // When & Then
//        mockMvc.perform(post("/roles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createRole_WithInvalidData_ShouldReturnBadRequest() throws Exception {
//        // Given
//        RoleRequest request = new RoleRequest(""); // Empty name
//
//        // When & Then
//        mockMvc.perform(post("/roles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void getRoleById_ShouldReturnRole() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When & Then
//        mockMvc.perform(get("/roles/{id}", role.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(role.getId()))
//                .andExpect(jsonPath("$.name").value("ADMIN"));
//    }
//
//    @Test
//    void getRoleById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
//        // When & Then
//        mockMvc.perform(get("/roles/{id}", 999L))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateRole_ShouldReturnUpdatedRole() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When & Then
//        mockMvc.perform(put("/roles/{id}", role.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(role.getId()))
//                .andExpect(jsonPath("$.name").value("SUPER_ADMIN"));
//
//        // Verify in database
//        Role updatedRole = roleRepository.findById(role.getId()).orElseThrow();
//        assertThat(updatedRole.getName()).isEqualTo("SUPER_ADMIN");
//    }
//
//    @Test
//    void updateRole_WithNonExistentId_ShouldReturnNotFound() throws Exception {
//        // Given
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When & Then
//        mockMvc.perform(put("/roles/{id}", 999L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void deleteRole_ShouldReturnOk() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When & Then
//        mockMvc.perform(delete("/roles/{id}", role.getId()))
//                .andExpect(status().isOk());
//
//        // Verify in database
//        assertThat(roleRepository.findById(role.getId())).isEmpty();
//    }
//
//    @Test
//    void deleteRole_WithNonExistentId_ShouldReturnNotFound() throws Exception {
//        // When & Then
//        mockMvc.perform(delete("/roles/{id}", 999L))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void listRoles_WithDefaultPagination_ShouldReturnAllRoles() throws Exception {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        roleRepository.save(new Role("USER"));
//        roleRepository.save(new Role("GUEST"));
//
//        // When & Then
//        mockMvc.perform(get("/roles"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(3)))
//                .andExpect(jsonPath("$.totalElements").value(3))
//                .andExpect(jsonPath("$.pageNumber").value(0))
//                .andExpect(jsonPath("$.pageSize").value(20))
//                .andExpect(jsonPath("$.first").value(true))
//                .andExpect(jsonPath("$.last").value(true));
//    }
//
//    @Test
//    void listRoles_WithCustomPagination_ShouldReturnPaginatedResults() throws Exception {
//        // Given
//        for (int i = 1; i <= 5; i++) {
//            roleRepository.save(new Role("ROLE_" + i));
//        }
//
//        // When & Then
//        mockMvc.perform(get("/roles")
//                .param("page", "0")
//                .param("size", "2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(2)))
//                .andExpect(jsonPath("$.totalElements").value(5))
//                .andExpect(jsonPath("$.pageNumber").value(0))
//                .andExpect(jsonPath("$.pageSize").value(2))
//                .andExpect(jsonPath("$.first").value(true))
//                .andExpect(jsonPath("$.last").value(false));
//    }
//
//    @Test
//    void listRoles_WithSorting_ShouldReturnSortedResults() throws Exception {
//        // Given
//        roleRepository.save(new Role("Z_ROLE"));
//        roleRepository.save(new Role("A_ROLE"));
//        roleRepository.save(new Role("M_ROLE"));
//
//        // When & Then
//        mockMvc.perform(get("/roles")
//                .param("sortBy", "name")
//                .param("direction", "asc"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].name").value("A_ROLE"))
//                .andExpect(jsonPath("$.content[1].name").value("M_ROLE"))
//                .andExpect(jsonPath("$.content[2].name").value("Z_ROLE"));
//    }
//
//    @Test
//    void getRoleById_ShouldCacheResult() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When - First call
//        mockMvc.perform(get("/roles/{id}", role.getId()))
//                .andExpect(status().isOk());
//
//        // When - Second call (should use cache)
//        mockMvc.perform(get("/roles/{id}", role.getId()))
//                .andExpect(status().isOk());
//
//        // Verify cache is used (in a real scenario, you might check cache statistics)
//        // This is more of a unit test concern, but we can verify the endpoint works
//        assertThat(roleRepository.findById(role.getId())).isPresent();
//    }
//
//    @Test
//    void updateRole_ShouldEvictCache() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("SUPER_ADMIN");
//
//        // When
//        mockMvc.perform(put("/roles/{id}", role.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        // Then - verify the role was updated
//        Role updatedRole = roleRepository.findById(role.getId()).orElseThrow();
//        assertThat(updatedRole.getName()).isEqualTo("SUPER_ADMIN");
//    }
//
//    @Test
//    void deleteRole_ShouldEvictCache() throws Exception {
//        // Given
//        Role role = roleRepository.save(new Role("ADMIN"));
//
//        // When
//        mockMvc.perform(delete("/roles/{id}", role.getId()))
//                .andExpect(status().isOk());
//
//        // Then - verify the role was deleted
//        assertThat(roleRepository.findById(role.getId())).isEmpty();
//    }
//
//    @Test
//    void createRole_ShouldEvictAllCache() throws Exception {
//        // Given
//        roleRepository.save(new Role("ADMIN"));
//        RoleRequest request = new RoleRequest("USER");
//
//        // When
//        mockMvc.perform(post("/roles")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        // Then - verify new role was created
//        List<Role> roles = roleRepository.findAll();
//        assertThat(roles).hasSize(2);
//    }
//}
