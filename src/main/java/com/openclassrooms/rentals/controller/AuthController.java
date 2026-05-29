package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.AuthResponse;
import com.openclassrooms.rentals.dto.LoginRequest;
import com.openclassrooms.rentals.dto.RegisterRequest;
import com.openclassrooms.rentals.dto.UserResponse;
import com.openclassrooms.rentals.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return null;
    }
}
