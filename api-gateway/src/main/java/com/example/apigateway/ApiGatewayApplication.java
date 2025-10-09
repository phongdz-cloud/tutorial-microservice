package com.example.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ApiGatewayApplication {
    public static void main(String[] args) {
        log.info("Starting ApiGatewayApplication v2");
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}


