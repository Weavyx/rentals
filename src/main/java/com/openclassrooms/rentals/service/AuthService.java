package com.openclassrooms.rentals.service;

import com.openclassrooms.rentals.dto.AuthRequest;
import com.openclassrooms.rentals.dto.AuthResponse;
import com.openclassrooms.rentals.dto.UserResponse;
import com.openclassrooms.rentals.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(AuthRequest request) {
        return null;
    }

    public AuthResponse login(AuthRequest request) {
        return null;
    }

    public UserResponse getCurrentUser(String email) {
        return null;
    }
}
