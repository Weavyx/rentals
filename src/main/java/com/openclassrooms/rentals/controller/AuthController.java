package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.AuthResponse;
import com.openclassrooms.rentals.dto.LoginRequest;
import com.openclassrooms.rentals.dto.RegisterRequest;
import com.openclassrooms.rentals.dto.UserResponse;
import com.openclassrooms.rentals.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Inscription, connexion et profil utilisateur")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Créer un compte", description = "Inscription d'un nouvel utilisateur. Retourne un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte créé, token retourné"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Se connecter", description = "Authentification par email et mot de passe. Retourne un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Profil courant", description = "Retourne le profil de l'utilisateur authentifié.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil retourné"),
            @ApiResponse(responseCode = "401", description = "Token absent ou invalide")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}
