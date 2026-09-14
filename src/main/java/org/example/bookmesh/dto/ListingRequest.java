package org.example.bookmesh.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ListingRequest(

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity
) {
}