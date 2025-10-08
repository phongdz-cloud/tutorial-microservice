package com.example.authservice.service;

import com.cursor.common.dto.UserResponse;
import com.cursor.common.jwt.JwtService;
import com.example.authservice.client.UserServiceClient;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;

    private final JwtService jwtService;

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    private final RetryRegistry retryRegistry;

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException {
        UserResponse userResponse = this.validateUser(loginRequest);

        if (userResponse.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(String.valueOf(userResponse.getId()),
                null, null);
        return new LoginResponse(
                token,
                3600L
        );
    }

    private UserResponse validateUser(LoginRequest loginRequest) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("user-service");
        Retry retry = retryRegistry.retry("user-service");

        Supplier<UserResponse> supplier = () -> {
            try {
                return userServiceClient.validateUser(loginRequest);
            } catch (Exception e) {
                throw new RuntimeException("User service call failed", e);
            }
        };

        // Apply circuit breaker and retry
        Supplier<UserResponse> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, supplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        return decoratedSupplier.get();

    }
}
