package com.internship.tool.controller;

import com.internship.tool.config.JwtUtil;
import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @PutMapping("/login")
    public Map<String, String> login(@RequestBody User request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username required");
        }

        String given = request.getPassword() != null ? request.getPassword() : "";
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        String stored = user != null && user.getPassword() != null ? user.getPassword() : "";

        if (user == null || !stored.equals(given)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password (try demo / demo; usernames are case-sensitive)");
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole());

        return Map.of("token", token);
    }
}