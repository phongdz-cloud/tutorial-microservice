package com.example.authservice.config;

import com.cursor.common.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${security.jwt.secret-base64}")
    String secret;
    @Value("${security.jwt.ttl-seconds}")
    long ttl;

    @Bean
    JwtService jwtService() {
        return new JwtService(secret, ttl);
    }

}
