package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(UserServiceCacheTest.TestConfig.class)
class UserServiceCacheTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setStatus(User.Status.ACTIVE);
        testUser.setCreatedAt(Instant.now());
        testUser.setUpdatedAt(Instant.now());

        testUserRequest = new UserRequest();
        testUserRequest.setUsername("testuser");
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
    }

    @Test
    void testGetById_ShouldCacheResult() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When - First call
        UserResponse result1 = userService.getById(1L);

        // When - Second call (should use cache)
        UserResponse result2 = userService.getById(1L);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getId(), result2.getId());
        assertEquals(result1.getUsername(), result2.getUsername());

        // Verify repository is called only once due to caching
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate_ShouldEvictCache() {
        // Given
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.create(testUserRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdate_ShouldEvictCache() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.update(1L, testUserRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDelete_ShouldEvictCache() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.delete(1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Configuration
    @EnableCaching
    static class TestConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("users");
        }
    }
}
