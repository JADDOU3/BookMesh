package org.example.bookmesh.controller.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.book.ListingRequest;
import org.example.bookmesh.dto.book.ListingResponse;
import org.example.bookmesh.model.User;
import org.example.bookmesh.service.book.BookListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookListingController {

    private final BookListingService bookListingService;

    @PostMapping("/api/books/{bookId}/listings")
    public ResponseEntity<ListingResponse> createListing(@PathVariable Long bookId,
                                                         @Valid @RequestBody ListingRequest request,
                                                         @AuthenticationPrincipal User supplier) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookListingService.createListing(bookId, request, supplier));
    }

    @GetMapping("/api/books/{bookId}/listings")
    public ResponseEntity<List<ListingResponse>> listListingsForBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookListingService.listListingsForBook(bookId));
    }

    @GetMapping("/api/suppliers/{supplierId}/listings")
    public ResponseEntity<List<ListingResponse>> listListingsForSupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(bookListingService.listListingsForSupplier(supplierId));
    }

    @PutMapping("/api/listings/{listingId}")
    public ResponseEntity<ListingResponse> updateStock(@PathVariable Long listingId,
                                                       @Valid @RequestBody ListingRequest request,
                                                       @AuthenticationPrincipal User requester) {
        return ResponseEntity.ok(bookListingService.updateStock(listingId, request, requester));
    }

    @DeleteMapping("/api/listings/{listingId}")
    public ResponseEntity<Void> deleteListing(@PathVariable Long listingId,
                                              @AuthenticationPrincipal User requester) {
        bookListingService.deleteListing(listingId, requester);
        return ResponseEntity.noContent().build();
    }
}