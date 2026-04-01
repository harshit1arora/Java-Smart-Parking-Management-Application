package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.model.User;
import com.smartparking.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signup(@RequestBody User user) {
        int userId = authService.signup(user);
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        return ResponseEntity.ok(ApiResponse.success(data, "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<User> userOpt = authService.login(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> data = new HashMap<>();
            data.put("user_id", user.getUserId());
            data.put("name", user.getName());
            data.put("role", user.getRole());
            return ResponseEntity.ok(ApiResponse.success(data, "Login successful!"));
        } else {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid email or password"));
        }
    }
}
