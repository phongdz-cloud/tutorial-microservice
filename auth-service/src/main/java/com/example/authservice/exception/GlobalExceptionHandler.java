package com.example.authservice.exception;

import com.cursor.common.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<UserResponse> handleCallNotPermittedException(CallNotPermittedException e) {
        UserResponse fallbackUser = new UserResponse(
                0L,
                "Service Unavailable",
                "service@unavailable.com",
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackUser);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UserResponse> handleRuntimeException(RuntimeException ex) {
        UserResponse errorResponse = new UserResponse(
                0L,
                "Internal Server Error",
                "internal@server.error",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
