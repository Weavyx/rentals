package com.openclassrooms.rentals.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotNull
    private Long rentalId;

    @NotNull
    private Long userId;

    @NotBlank
    private String message;
}
