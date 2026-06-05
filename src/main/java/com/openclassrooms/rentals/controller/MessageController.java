package com.openclassrooms.rentals.controller;

import com.openclassrooms.rentals.dto.MessageRequest;
import com.openclassrooms.rentals.dto.MessageResponse;
import com.openclassrooms.rentals.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Envoyer un message", description = "Le user_id du body est validé contre le token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message envoyé"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou user_id ne correspond pas au token")
    })
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.sendMessage(request, authentication.getName()));
    }
}
