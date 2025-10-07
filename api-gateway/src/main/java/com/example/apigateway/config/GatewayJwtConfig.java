package com.example.apigateway.config;

import com.cursor.common.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayJwtConfig {

    @Value("${security.jwt.secret-base64}")
    String secret;

    @Bean
    JwtService jwtService() {
        return new JwtService(secret, 3600);
    } // ttl not used here

}
