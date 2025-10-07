package com.example.userservice.controller;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.enum_.StatusEnum;
import com.cursor.common.pagination.PageResponse;
import com.cursor.common.pagination.PageUtils;
import com.example.userservice.dto.RoleDto;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@Tag(name = "Role Management", description = "APIs for managing roles")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Create a new role", description = "Creates a new role with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class), examples = @ExampleObject(value = """
                    {
                        "status": "SUCCESS",
                        "code": "SUCCESS",
                        "message": "Role created",
                        "data": {
                            "id": 1,
                            "name": "admin",
                        }
                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation error or conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping
    public BaseResponse<RoleDto> create(@Parameter(description = "Role information", required = true)
                                        @Valid @RequestBody RoleRequest roleRequest) {
        RoleDto roleDto = roleService.createRole(roleRequest);
        return BaseResponse.<RoleDto>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("Role created")
                .data(roleDto)
                .build();
    }

    @Operation(summary = "Get role by ID", description = "Retrieves a role by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public BaseResponse<RoleDto> getById(
            @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id) {
        RoleDto roleDto = roleService.getById(id);
        return BaseResponse.<RoleDto>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(roleDto)
                .build();
    }

    @Operation(summary = "Update role", description = "Updates an existing role with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "400", description = "Validation error or conflict")
    })
    @PutMapping("/{id}")
    public BaseResponse<RoleDto> update(
            @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Updated role information", required = true) @Valid @RequestBody RoleRequest request) {
        RoleDto roleDto = roleService.updateRole(id, request);
        return BaseResponse.<RoleDto>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("Role updated")
                .data(roleDto)
                .build();
    }

    @Operation(summary = "Delete role", description = "Soft deletes a role by setting status to INACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(
            @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id) {
        roleService.delete(id);
        return BaseResponse.<Void>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("Role deleted")
                .build();
    }

    @Operation(summary = "List roles with pagination", description = "Retrieves a paginated list of all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    @GetMapping
    public BaseResponse<PageResponse<RoleDto>> list(
            @Parameter(description = "page", example = "1") @RequestParam(required = false) Integer page,
            @Parameter(description = "size", example = "10") @RequestParam(required = false) Integer size,
            @Parameter(description = "sortBy") @RequestParam(required = false) String sortBy,
            @Parameter(description = "direction") @RequestParam(required = false) String direction) {
        Pageable pageable = PageUtils.buildPageable(page, size, sortBy, direction);
        PageResponse<RoleDto> pageResponse = roleService.getAll(pageable);
        return BaseResponse.<PageResponse<RoleDto>>builder()
                .status(StatusEnum.SUCCESS)
                .code("SUCCESS")
                .message("OK")
                .data(pageResponse)
                .build();
    }

}
