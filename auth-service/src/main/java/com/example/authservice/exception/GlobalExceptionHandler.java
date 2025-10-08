package com.example.authservice.exception;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.dto.UserResponse;
import com.cursor.common.enum_.StatusEnum;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<BaseResponse<UserResponse>> handleCallNotPermittedException(CallNotPermittedException e) {
        UserResponse fallbackUser = new UserResponse(
                0L,
                "Service Unavailable",
                "service@unavailable.com",
                "unknow"
        );
        BaseResponse<UserResponse> fallbackUserResponse = BaseResponse.<UserResponse>builder()
                .status(StatusEnum.ERROR)
                .code("ERROR")
                .message("Service unavailable")
                .data(fallbackUser)
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackUserResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleRuntimeException(RuntimeException ex) {
        BaseResponse<UserResponse> errorResponse = BaseResponse.<UserResponse>builder()
                .status(StatusEnum.ERROR)
                .code("ERROR")
                .message("Internal Server Error")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
