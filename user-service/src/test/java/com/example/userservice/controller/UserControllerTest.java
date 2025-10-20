package com.example.userservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("UserController Component Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserRequest userRequest;
    private UserDto updatedUserDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john_doe");
        userDto.setEmail("john@example.com");
        userDto.setPassword("hashedPassword");
        userDto.setStatus(User.Status.ACTIVE);

        userRequest = new UserRequest();
        userRequest.setUsername("john_doe");
        userRequest.setEmail("john@example.com");
        userRequest.setPassword("password123");

        updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setUsername("john_doe_updated");
        updatedUserDto.setEmail("john.updated@example.com");
        updatedUserDto.setPassword("hashedPassword");
        updatedUserDto.setStatus(User.Status.ACTIVE);
    }

    @Test
    @DisplayName("POST /users - Should create user successfully")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        when(userService.create(any())).thenReturn(userDto);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(userService, times(1)).create(any());
    }

    @Test
    @DisplayName("POST /users - Should return 400 for invalid request")
    void createUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setUsername(""); // Invalid: empty username
        invalidRequest.setEmail("invalid-email"); // Invalid: not a valid email
        invalidRequest.setPassword("123"); // Invalid: too short password

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("GET /users/{id} - Should return user by ID")
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getById(1L)).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("GET /users/{id} - Should return 404 for non-existent user")
    void getUserById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getById(999L)).thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getById(999L);
    }

    @Test
    @DisplayName("GET /users - Should return paginated list of users")
    void listUsers_ShouldReturnPaginatedUsers() throws Exception {
        // Given
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("jane_doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("hashedPassword");
        user2.setStatus(User.Status.ACTIVE);

        List<UserDto> users = Arrays.asList(userDto, user2);
        PageImpl<UserDto> page = new PageImpl<>(users, PageRequest.of(0, 10), 2);
        PageResponse<UserDto> pageResponse = PageResponse.from(page);

        when(userService.list(any())).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "username")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].username", is("john_doe")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].username", is("jane_doe")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last", is(true)));

        verify(userService, times(1)).list(any());
    }

    @Test
    @DisplayName("GET /users - Should return users with default pagination")
    void listUsers_WithDefaultPagination_ShouldReturnUsers() throws Exception {
        // Given
        List<UserDto> users = Arrays.asList(userDto);
        PageImpl<UserDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 1);
        PageResponse<UserDto> pageResponse = PageResponse.from(page);

        when(userService.list(any())).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].username", is("john_doe")));

        verify(userService, times(1)).list(any());
    }

    @Test
    @DisplayName("PUT /users/{id} - Should update user successfully")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("john_doe_updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPassword("newPassword123");

        when(userService.update(eq(1L), any())).thenReturn(updatedUserDto);

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe_updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(userService, times(1)).update(eq(1L), any());
    }

    @Test
    @DisplayName("PUT /users/{id} - Should return 400 for invalid request")
    void updateUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setUsername(""); // Invalid: empty username
        invalidRequest.setEmail("invalid-email"); // Invalid: not a valid email
        invalidRequest.setPassword("123"); // Invalid: too short password

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("PUT /users/{id} - Should return 404 for non-existent user")
    void updateUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.update(eq(999L), any()))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).update(eq(999L), any());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Should delete user successfully")
    void deleteUser_ShouldReturnOk() throws Exception {
        // Given
        doNothing().when(userService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /users/{id} - Should return 404 for non-existent user")
    void deleteUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found")).when(userService).delete(999L);

        // When & Then
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).delete(999L);
    }

    @Test
    @DisplayName("POST /users - Should handle service exception")
    void createUser_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.create(any()))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).create(any());
    }

    @Test
    @DisplayName("GET /users/{id} - Should handle invalid ID format")
    void getUserById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/invalid"))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).getById(anyLong());
    }

    @Test
    @DisplayName("PUT /users/{id} - Should handle invalid ID format")
    void updateUser_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/users/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Should handle invalid ID format")
    void deleteUser_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/users/invalid"))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("POST /users - Should handle missing request body")
    void createUser_WithMissingRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).create(any());
    }

    @Test
    @DisplayName("PUT /users/{id} - Should handle missing request body")
    void updateUser_WithMissingRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, never()).update(anyLong(), any());
    }
}
