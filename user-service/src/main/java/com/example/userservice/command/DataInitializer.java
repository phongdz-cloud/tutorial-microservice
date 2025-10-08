package com.example.userservice.command;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initAdminUser() {
        return args -> {
            if(userRepository.findByUsername(adminUsername).isEmpty()) {
                User user = new User();
                user.setUsername(adminUsername);
                user.setEmail(adminUsername + "@gmail.com");
                user.setPassword(passwordEncoder.encode(adminPassword));
                userRepository.save(user);
            }
        };
    }
}
