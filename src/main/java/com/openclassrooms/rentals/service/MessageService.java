package com.openclassrooms.rentals.service;

import com.openclassrooms.rentals.dto.MessageRequest;
import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.repository.MessageRepository;
import com.openclassrooms.rentals.repository.RentalRepository;
import com.openclassrooms.rentals.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public MessageResponse sendMessage(MessageRequest request, String userEmail) {
        return null;
    }
}
