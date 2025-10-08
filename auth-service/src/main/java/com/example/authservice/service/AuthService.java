package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest) throws InvalidCredentialsException;

}
