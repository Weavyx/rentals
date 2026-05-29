package com.openclassrooms.rentals.repository;

import com.openclassrooms.rentals.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
