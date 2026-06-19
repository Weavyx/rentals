package com.openclassrooms.rentals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("rental_id")
    private Long rentalId;

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank
    private String message;
}
