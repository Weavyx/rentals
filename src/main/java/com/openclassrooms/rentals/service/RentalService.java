package com.openclassrooms.rentals.service;

import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.dto.RentalResponse;
import com.openclassrooms.rentals.dto.RentalsResponse;
import com.openclassrooms.rentals.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public RentalsResponse getAllRentals() {
        return null;
    }

    public RentalResponse getRentalById(Long id) {
        return null;
    }

    public MessageResponse createRental(String name, Double surface, Double price, MultipartFile picture, String description, String ownerEmail) {
        return null;
    }

    public MessageResponse updateRental(Long id, String name, Double surface, Double price, String description) {
        return null;
    }
}
