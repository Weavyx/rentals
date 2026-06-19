package com.openclassrooms.rentals.service;

import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.dto.RentalResponse;
import com.openclassrooms.rentals.dto.RentalsResponse;
import com.openclassrooms.rentals.model.Rental;
import com.openclassrooms.rentals.model.User;
import com.openclassrooms.rentals.repository.RentalRepository;
import com.openclassrooms.rentals.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:3001}")
    private String baseUrl;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /** Retourne la liste de toutes les annonces de location. */
    public RentalsResponse getAllRentals() {
        List<RentalResponse> list = rentalRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new RentalsResponse(list);
    }

    /** Retourne une annonce par son identifiant, ou 404 si introuvable. */
    public RentalResponse getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));
        return toResponse(rental);
    }

    /** Crée une nouvelle annonce de location avec upload de l'image associée. */
    public MessageResponse createRental(String name, Double surface, Double price,
                                        MultipartFile picture, String description,
                                        String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String pictureUrl;
        try {
            String originalFilename = picture.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(picture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            pictureUrl = baseUrl + "/uploads/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file", e);
        }

        Rental rental = Rental.builder()
                .name(name)
                .surface(surface)
                .price(price)
                .picture(pictureUrl)
                .description(description)
                .owner(owner)
                .build();
        rentalRepository.save(rental);

        return new MessageResponse("Rental created !");
    }

    /** Met à jour les champs textuels d'une annonce existante (l'image n'est pas modifiée). */
    public MessageResponse updateRental(Long id, String name, Double surface, Double price,
                                        String description, String ownerEmail) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        if (!rental.getOwner().getEmail().equals(ownerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the owner of this rental");
        }

        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rentalRepository.save(rental);

        return new MessageResponse("Rental updated !");
    }

    private RentalResponse toResponse(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getName(),
                rental.getSurface(),
                rental.getPrice(),
                rental.getPicture(),
                rental.getDescription(),
                rental.getOwner().getId(),
                rental.getCreatedAt(),
                rental.getUpdatedAt()
        );
    }
}
