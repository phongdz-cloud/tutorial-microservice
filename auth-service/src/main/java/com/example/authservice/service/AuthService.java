package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

}
