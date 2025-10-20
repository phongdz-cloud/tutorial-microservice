package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cursor.common.dto.UserResponse;
import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.cursor.common.pagination.PageResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.impl.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    private UserDto userDto;
    private LoginRequest loginRequest;

    @BeforeEach
    void setup() {
        userRequest = new UserRequest();
        userRequest.setUsername("john");
        userRequest.setEmail("john@example.com");
        userRequest.setPassword("secret123");

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setStatus(User.Status.ACTIVE);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");
        userDto.setEmail("john@example.com");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("john");
        loginRequest.setPassword("secret123");
    }

    // ========== CREATE METHOD TESTS ==========

    @Test
    void create_success() {
        // Given
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.create(userRequest);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository).findByUsername("john");
        verify(userRepository).findByEmail("john@example.com");
        verify(userMapper).toEntity(userRequest);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void create_usernameConflict_throwsBusinessException() {
        // Given
        User existingUser = new User();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.create(userRequest));

        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        assertEquals("Username already exists", ex.getMessage());

        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_emailConflict_throwsBusinessException() {
        // Given
        User existingUser = new User();
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.create(userRequest));

        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        assertEquals("Email already exists", ex.getMessage());

        verify(userRepository).findByUsername("john");
        verify(userRepository).findByEmail("john@example.com");
        verify(userRepository, never()).save(any());
    }

    // ========== GET BY ID METHOD TESTS ==========

    @Test
    void getById_success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.getById(1L);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void getById_notFound_throwsBusinessException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.getById(1L));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(userMapper, never()).toDto(any());
    }

    // ========== LIST METHOD TESTS ==========

    @Test
    void list_success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        PageResponse<UserDto> result = userService.list(pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.getContent().isEmpty());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void list_emptyResult() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of());
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        PageResponse<UserDto> result = userService.list(pageable);

        // Then
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(userRepository).findAll(pageable);
    }

    // ========== UPDATE METHOD TESTS ==========

    @Test
    void update_success() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("johnny");
        updateRequest.setEmail("johnny@example.com");
        updateRequest.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("johnny")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("johnny@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.update(1L, updateRequest);

        // Then
        assertNotNull(result);

        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("johnny");
        verify(userRepository).findByEmail("johnny@example.com");
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);

        // Verify user properties were updated
        assertEquals("johnny", user.getUsername());
        assertEquals("johnny@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void update_sameUsernameAndEmail_success() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("john"); // Same username
        updateRequest.setEmail("john@example.com"); // Same email
        updateRequest.setPassword("newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.update(1L, updateRequest);

        // Then
        assertNotNull(result);

        verify(userRepository).findById(1L);
        // Should not check for conflicts when username/email are the same
        verify(userRepository, never()).findByUsername("john");
        verify(userRepository, never()).findByEmail("john@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void update_usernameConflict_throwsBusinessException() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("existingUser");
        updateRequest.setEmail("john@example.com");

        User conflictUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(conflictUser));

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.update(1L, updateRequest));

        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        assertEquals("Username already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_emailConflict_throwsBusinessException() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("john");
        updateRequest.setEmail("existing@example.com");

        User conflictUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(conflictUser));

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.update(1L, updateRequest));

        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        assertEquals("Email already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_userNotFound_throwsBusinessException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.update(1L, userRequest));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_emptyPassword_doesNotUpdatePassword() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("john");
        updateRequest.setEmail("john@example.com");
        updateRequest.setPassword(""); // Empty password

        String originalPassword = user.getPassword();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        userService.update(1L, updateRequest);

        // Then
        assertEquals(originalPassword, user.getPassword()); // Password should remain unchanged
        verify(userRepository).save(user);
    }

    @Test
    void update_nullPassword_doesNotUpdatePassword() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("john");
        updateRequest.setEmail("john@example.com");
        updateRequest.setPassword(null); // Null password

        String originalPassword = user.getPassword();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        userService.update(1L, updateRequest);

        // Then
        assertEquals(originalPassword, user.getPassword()); // Password should remain unchanged
        verify(userRepository).save(user);
    }

    // ========== DELETE METHOD TESTS ==========

    @Test
    void delete_success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.delete(1L);

        // Then
        assertEquals(User.Status.INACTIVE, user.getStatus());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void delete_userNotFound_throwsBusinessException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.delete(1L));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    // ========== VALIDATE USER METHOD TESTS ==========

    @Test
    void validateUser_success() {
        // Given
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "encodedPassword")).thenReturn(true);

        // When
        UserResponse result = userService.validateUser(loginRequest);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(HttpStatus.OK.value(), result.getStatus());

        verify(userRepository).findByUsername("john");
        verify(passwordEncoder).matches("secret123", "encodedPassword");
    }

    @Test
    void validateUser_invalidPassword_returnsUnauthorized() {
        // Given
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        loginRequest.setPassword("wrongPassword");

        // When
        UserResponse result = userService.validateUser(loginRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getStatus());
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());

        verify(userRepository).findByUsername("john");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void validateUser_userNotFound_returnsUnauthorized() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        loginRequest.setUsername("nonexistent");

        // When
        UserResponse result = userService.validateUser(loginRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getStatus());
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    void create_nullRequest_throwsException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.create(null));
    }

    @Test
    void getById_nullId_throwsException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.getById(null));
    }

    @Test
    void update_nullId_throwsException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.update(null, userRequest));
    }

    @Test
    void delete_nullId_throwsException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.delete(null));
    }

    @Test
    void validateUser_nullRequest_throwsException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.validateUser(null));
    }
}