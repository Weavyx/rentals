package com.openclassrooms.rentals.service;

import com.openclassrooms.rentals.dto.MessageRequest;
import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.model.Message;
import com.openclassrooms.rentals.model.Rental;
import com.openclassrooms.rentals.model.User;
import com.openclassrooms.rentals.repository.MessageRepository;
import com.openclassrooms.rentals.repository.RentalRepository;
import com.openclassrooms.rentals.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    /** Envoie un message lié à une annonce, après validation que l'expéditeur correspond au token JWT. */
    public MessageResponse sendMessage(MessageRequest request, String userEmail) {
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!request.getUserId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "user_id in body does not match authenticated user");
        }

        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found"));

        Message message = Message.builder()
                .user(authenticatedUser)
                .rental(rental)
                .message(request.getMessage())
                .build();
        messageRepository.save(message);

        return new MessageResponse("Message send with success");
    }
}
