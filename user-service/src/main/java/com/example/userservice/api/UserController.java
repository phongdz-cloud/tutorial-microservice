package com.example.userservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(
                Map.of(
                        "users", new String[]{"alice", "bob", "charlie"}
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return ResponseEntity.ok(
                Map.of(
                        "id", id,
                        "name", "user-" + id
                )
        );
    }
}


