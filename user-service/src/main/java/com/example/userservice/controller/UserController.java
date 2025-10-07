package com.example.userservice.controller;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.dto.PageResponse;
import com.cursor.common.enum_.StatusEnum;
import com.cursor.common.util.PaginationUtil;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class), examples = @ExampleObject(value = """
                    {
                        "status": "SUCCESS",
                        "code": "SUCCESS",
                        "message": "User created",
                        "data": {
                            "id": 1,
                            "username": "john_doe",
                            "email": "john@example.com",
                            "status": "ACTIVE",
                            "createdAt": "2024-01-01T12:00:00Z",
                            "updatedAt": "2024-01-01T12:00:00Z"
                        }
                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation error or conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping
    public BaseResponse<UserResponse> create(
            @Parameter(description = "User information", required = true) @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User created")
                .data(response)
                .build();
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public BaseResponse<UserResponse> getById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {
        UserResponse response = userService.getById(id);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(response)
                .build();
    }

    @Operation(summary = "List users with pagination", description = "Retrieves a paginated list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public BaseResponse<PageResponse<UserResponse>> list(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<UserResponse> page = userService.list(pageable);
        PageResponse<UserResponse> pageResponse = PaginationUtil.toPageResponse(page);
        return BaseResponse.<PageResponse<UserResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(pageResponse)
                .build();
    }

    @Operation(summary = "Update user", description = "Updates an existing user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Validation error or conflict")
    })
    @PutMapping("/{id}")
    public BaseResponse<UserResponse> update(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Updated user information", required = true) @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.update(id, request);
        return BaseResponse.<UserResponse>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User updated")
                .data(response)
                .build();
    }

    @Operation(summary = "Delete user", description = "Soft deletes a user by setting status to INACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {
        userService.delete(id);
        return BaseResponse.<Void>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("User deleted")
                .build();
    }
}
