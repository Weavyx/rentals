package com.openclassrooms.rentals.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateToken(UserDetails userDetails) {
        return null;
    }

    public String extractEmail(String token) {
        return null;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return false;
    }
}
