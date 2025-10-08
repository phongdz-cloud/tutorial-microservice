package com.example.authservice.controller;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.enum_.StatusEnum;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Management", description = "APIs for managing auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User Login", description = "API User Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class), examples = @ExampleObject(value = """
                    {
                        "status": "SUCCESS",
                        "code": "SUCCESS",
                        "message": "User created",
                        "data": {
                            "token": "abc",
                            "ttl": 3600
                        }
                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

//        if(!req.getPassword().equals("password")) return BaseResponse.<LoginResponse>builder()
//                .status(StatusEnum.ERROR)
//                .code("SUCCESS")
//                .message("Username or password is incorrect")
//                .data(null)
//                .build();
//        String userId = "1";
//        List<String> roles = List.of("USER");
//        String token = jwtService.generateToken(userId, roles, Map.of("username", req.getUsername()));
        LoginResponse login = authService.login(req);
        return BaseResponse.<LoginResponse>builder()
                .status(StatusEnum.ERROR)
                .code("SUCCESS")
                .message("User login successful")
                .data(new LoginResponse(login.getToken(), 3600))
                .build();
    }
}
