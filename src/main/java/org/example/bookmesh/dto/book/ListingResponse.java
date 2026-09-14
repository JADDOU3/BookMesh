package org.example.bookmesh.dto.book;

public record ListingResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long supplierId,
        String supplierName,
        int stockQuantity
) {
}