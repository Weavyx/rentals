package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.MessageRequest;
import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        return null;
    }
}
