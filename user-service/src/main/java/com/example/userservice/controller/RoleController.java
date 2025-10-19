package com.example.userservice.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@Tag(name = "Role Management", description = "APIs for managing roles")
@AllArgsConstructor
public class RoleController {

        private final RoleService roleService;

        @Operation(summary = "Create a new role", description = "Creates a new role with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class), examples = @ExampleObject(value = """
                                        {
                                            "status": "SUCCESS",
                                            "code": "SUCCESS",
                                            "message": "Role created",
                                            "data": {
                                                "id": 1,
                                                "name": "admin",
                                            }
                                        }"""))),
                        @ApiResponse(responseCode = "400", description = "Validation error or conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)))
        })
        @PostMapping
        public ResponseEntity<RoleDto> create(
                        @Parameter(description = "Role information", required = true) @Valid @RequestBody RoleRequest roleRequest) {
                RoleDto roleDto = roleService.createRole(roleRequest);
                return ResponseEntity.ok(roleDto);
        }

        @Operation(summary = "Get role by ID", description = "Retrieves a role by their unique identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role found successfully"),
                        @ApiResponse(responseCode = "404", description = "Role not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<RoleDto> getById(
                        @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id) {
                RoleDto roleDto = roleService.getById(id);
                return ResponseEntity.ok(roleDto);
        }

        @Operation(summary = "Update role", description = "Updates an existing role with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Role not found"),
                        @ApiResponse(responseCode = "400", description = "Validation error or conflict")
        })
        @PutMapping("/{id}")
        public ResponseEntity<RoleDto> update(
                        @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id,
                        @Parameter(description = "Updated role information", required = true) @Valid @RequestBody RoleRequest request) {
                RoleDto roleDto = roleService.updateRole(id, request);
                return ResponseEntity.ok(roleDto);
        }

        @Operation(summary = "Delete role", description = "Soft deletes a role by setting status to INACTIVE")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Role not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Long id) {
                roleService.delete(id);
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "List roles with pagination", description = "Retrieves a paginated list of all roles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
        })
        @GetMapping
        public ResponseEntity<PageResponse<RoleDto>> list(
                        @Parameter(description = "page", example = "1") @RequestParam(required = false) Integer page,
                        @Parameter(description = "size", example = "10") @RequestParam(required = false) Integer size,
                        @Parameter(description = "sortBy") @RequestParam(required = false) String sortBy,
                        @Parameter(description = "direction") @RequestParam(required = false) String direction) {
                Pageable pageable = PageUtils.buildPageable(page, size, sortBy, direction);
                PageResponse<RoleDto> pageResponse = roleService.getAll(pageable);
                return ResponseEntity.ok(pageResponse);
        }

}
