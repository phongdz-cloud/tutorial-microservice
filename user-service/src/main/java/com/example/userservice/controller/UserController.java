package com.example.userservice.controller;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.enum_.StatusEnum;
import com.cursor.common.pagination.PageResponse;
import com.cursor.common.pagination.PageUtils;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserDto;
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
    public BaseResponse<UserDto> create(
            @Parameter(description = "User information", required = true) @Valid @RequestBody UserRequest request) {
        UserDto response = userService.create(request);
        return BaseResponse.<UserDto>builder()
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
    public BaseResponse<UserDto> getById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {
        UserDto response = userService.getById(id);
        return BaseResponse.<UserDto>builder()
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
    public BaseResponse<PageResponse<UserDto>> list(
            @Parameter(description = "page", example = "1") @RequestParam(required = false) Integer page,
            @Parameter(description = "size", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "sortBy") @RequestParam(required = false) String sortBy,
            @Parameter(description = "direction") @RequestParam(required = false) String direction) {
        Pageable pageable = PageUtils.buildPageable(page, size, sortBy, direction);
        PageResponse<UserDto> pageResponse = userService.list(pageable);
        return BaseResponse.<PageResponse<UserDto>>builder()
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
    public BaseResponse<UserDto> update(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Updated user information", required = true) @Valid @RequestBody UserRequest request) {
        UserDto response = userService.update(id, request);
        return BaseResponse.<UserDto>builder()
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
