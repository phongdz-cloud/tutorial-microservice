package com.example.userservice.controller;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.dto.PageResponse;
import com.cursor.common.enum_.StatusEnum;
import com.cursor.common.util.PaginationUtil;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public BaseResponse<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User created")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<UserResponse> getById(@PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(response)
                .build();
    }

    @GetMapping
    public BaseResponse<PageResponse<UserResponse>> list(Pageable pageable) {
        Page<UserResponse> page = userService.list(pageable);
        PageResponse<UserResponse> pageResponse = PaginationUtil.toPageResponse(page);
        return BaseResponse.<PageResponse<UserResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(pageResponse)
                .build();
    }

    @PutMapping("/{id}")
    public BaseResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.update(id, request);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User updated")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return BaseResponse.<Void>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User deleted")
                .build();
    }
}
