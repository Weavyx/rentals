package com.openclassrooms.rentals.repository;

import com.openclassrooms.rentals.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
