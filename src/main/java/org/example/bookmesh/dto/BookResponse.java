package org.example.bookmesh.dto;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        Long authorId,
        String authorName
) {
}