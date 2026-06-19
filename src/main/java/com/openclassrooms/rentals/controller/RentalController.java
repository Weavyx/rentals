package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.dto.RentalResponse;
import com.openclassrooms.rentals.dto.RentalsResponse;
import com.openclassrooms.rentals.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Gestion des locations")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /** Retourne la liste de toutes les annonces de location. */
    @Operation(summary = "Liste des locations")
    @ApiResponse(responseCode = "200", description = "Liste retournée")
    @GetMapping
    public ResponseEntity<RentalsResponse> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    /** Retourne une annonce par son identifiant, ou 404 si elle n'existe pas. */
    @Operation(summary = "Détail d'une location")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location trouvée"),
            @ApiResponse(responseCode = "404", description = "Location introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    /** Crée une nouvelle annonce avec upload de l'image, en utilisant l'email du token JWT comme propriétaire. */
    @Operation(summary = "Créer une location", description = "Crée une location avec upload d'image (multipart/form-data). L'owner_id est extrait du token JWT.")
    @ApiResponse(responseCode = "200", description = "Location créée")
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
    @Operation(summary = "Modifier une location", description = "Met à jour name, surface, price, description. L'image existante est conservée.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location mise à jour"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas le propriétaire"),
            @ApiResponse(responseCode = "404", description = "Location introuvable")
    })
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
