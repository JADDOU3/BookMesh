package org.example.bookmesh.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.ListingRequest;
import org.example.bookmesh.dto.ListingResponse;
import org.example.bookmesh.exception.DuplicateListingException;
import org.example.bookmesh.exception.ForbiddenOperationException;
import org.example.bookmesh.exception.ResourceNotFoundException;
import org.example.bookmesh.model.Book;
import org.example.bookmesh.model.BookListing;
import org.example.bookmesh.model.Role;
import org.example.bookmesh.model.User;
import org.example.bookmesh.repository.BookListingRepository;
import org.example.bookmesh.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookListingService {

    private final BookListingRepository bookListingRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ListingResponse createListing(Long bookId, ListingRequest request, User supplier) {
        requireRole(supplier, Role.SUPPLIER);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No book found with id " + bookId));

        if (bookListingRepository.existsByBookIdAndSupplierId(bookId, supplier.getId())) {
            throw new DuplicateListingException("You already have a listing for this book");
        }

        BookListing listing = BookListing.builder()
                .book(book)
                .supplier(supplier)
                .stockQuantity(request.stockQuantity())
                .build();

        return toResponse(bookListingRepository.save(listing));
    }

    public List<ListingResponse> listListingsForBook(Long bookId) {
        return bookListingRepository.findByBookId(bookId).stream().map(this::toResponse).toList();
    }

    public List<ListingResponse> listListingsForSupplier(Long supplierId) {
        return bookListingRepository.findBySupplierId(supplierId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ListingResponse updateStock(Long listingId, ListingRequest request, User requester) {
        BookListing listing = findListingOrThrow(listingId);
        requireOwner(listing, requester);

        listing.setStockQuantity(request.stockQuantity());
        return toResponse(bookListingRepository.save(listing));
    }

    @Transactional
    public void deleteListing(Long listingId, User requester) {
        BookListing listing = findListingOrThrow(listingId);
        requireOwner(listing, requester);
        bookListingRepository.delete(listing);
    }

    private BookListing findListingOrThrow(Long listingId) {
        return bookListingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("No listing found with id " + listingId));
    }

    private void requireOwner(BookListing listing, User requester) {
        if (!listing.getSupplier().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException("You do not own this listing");
        }
    }

    private void requireRole(User user, Role role) {
        if (user.getRole() != role) {
            throw new ForbiddenOperationException("Only a " + role.name().toLowerCase() + " can perform this action");
        }
    }

    private ListingResponse toResponse(BookListing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getBook().getId(),
                listing.getBook().getTitle(),
                listing.getSupplier().getId(),
                listing.getSupplier().getFullName(),
                listing.getStockQuantity()
        );
    }
}