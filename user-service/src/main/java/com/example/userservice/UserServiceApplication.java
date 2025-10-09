package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class UserServiceApplication {
    public static void main(String[] args) {
        log.info("Starting UserServiceApplication v3");
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
