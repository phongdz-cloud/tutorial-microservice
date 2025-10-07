package com.example.userservice.service;

import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.impl.UserServiceImpl;
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

public class UserServiceImplTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
//        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void create_success() {
        UserRequest req = new UserRequest();
        req.setUsername("john");
        req.setEmail("john@example.com");
        req.setPassword("secret123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("john");
        saved.setEmail("john@example.com");
        saved.setPassword("secret123");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var res = userService.create(req);
        assertEquals(1L, res.getId());
        assertEquals("john", res.getUsername());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("john", captor.getValue().getUsername());
    }

    @Test
    void create_conflict_username() {
        UserRequest req = new UserRequest();
        req.setUsername("john");
        req.setEmail("john@example.com");
        req.setPassword("secret123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.create(req));
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
    }

    @Test
    void getById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getById(1L));
    }

    @Test
    void list_success() {
        User u = new User();
        u.setId(1L);
        u.setUsername("john");
        u.setEmail("john@example.com");
        Page<User> page = new PageImpl<>(List.of(u));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);
        var res = userService.list(PageRequest.of(0, 10));
        assertEquals(1, res.getTotalElements());
    }

    @Test
    void update_success() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("john");
        existing.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserRequest req = new UserRequest();
        req.setUsername("johnny");
        req.setEmail("johnny@example.com");
        req.setPassword("newpass");

        var res = userService.update(1L, req);
        assertEquals("johnny", res.getUsername());
        assertEquals("johnny@example.com", res.getEmail());
    }

    @Test
    void delete_soft() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("john");
        existing.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        userService.delete(1L);
        assertEquals(User.Status.INACTIVE, existing.getStatus());
        verify(userRepository).save(existing);
    }
}
