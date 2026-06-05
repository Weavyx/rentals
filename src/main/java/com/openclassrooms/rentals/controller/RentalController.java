package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.dto.RentalResponse;
import com.openclassrooms.rentals.dto.RentalsResponse;
import com.openclassrooms.rentals.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /** Retourne la liste de toutes les annonces de location. */
    @GetMapping
    public ResponseEntity<RentalsResponse> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    /** Retourne une annonce par son identifiant, ou 404 si elle n'existe pas. */
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    /** Crée une nouvelle annonce avec upload de l'image, en utilisant l'email du token JWT comme propriétaire. */
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createRental(
            @RequestParam String name,
            @RequestParam Double surface,
            @RequestParam Double price,
            @RequestParam MultipartFile picture,
            @RequestParam String description,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.createRental(name, surface, price, picture, description, authentication.getName()));
    }

    /** Met à jour les champs textuels d'une annonce existante, en vérifiant que l'appelant en est le propriétaire. */
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateRental(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double surface,
            @RequestParam Double price,
            @RequestParam String description,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.updateRental(id, name, surface, price, description, authentication.getName()));
    }
}
