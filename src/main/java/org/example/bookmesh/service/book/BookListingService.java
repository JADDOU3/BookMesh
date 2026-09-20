package org.example.bookmesh.service.book;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.book.ListingRequest;
import org.example.bookmesh.dto.book.ListingResponse;
import org.example.bookmesh.exception.DuplicateListingException;
import org.example.bookmesh.exception.ForbiddenOperationException;
import org.example.bookmesh.exception.ResourceNotFoundException;
import org.example.bookmesh.model.book.Book;
import org.example.bookmesh.model.book.BookListing;
import org.example.bookmesh.model.Role;
import org.example.bookmesh.model.User;
import org.example.bookmesh.repository.book.BookListingRepository;
import org.example.bookmesh.repository.book.BookRepository;
import org.example.bookmesh.util.SecurityUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookListingService {

    private final BookListingRepository bookListingRepository;
    private final BookRepository bookRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "listingList", allEntries = true),
            @CacheEvict(value = "listingListBySupplier", allEntries = true)
    })
    public ListingResponse createListing(ListingRequest request) {
        User supplier = SecurityUtils.getCurrentUser();

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("No book found with id " + request.bookId()));

        if (bookListingRepository.existsByBookIdAndSupplierId(request.bookId(), supplier.getId())) {
            throw new DuplicateListingException("You already have a listing for this book");
        }

        BookListing listing = BookListing.builder()
                .book(book)
                .supplier(supplier)
                .stockQuantity(request.stockQuantity())
                .build();

        return toResponse(bookListingRepository.save(listing));
    }

    @Cacheable(value = "listingList", key="#bookId")
    public List<ListingResponse> listListingsForBook(Long bookId) {
        return bookListingRepository.findByBookId(bookId).stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "listingListBySupplier", key="#supplierId")
    public List<ListingResponse> listListingsForSupplier(Long supplierId) {
        return bookListingRepository.findBySupplierId(supplierId).stream().map(this::toResponse).toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "listingList", allEntries = true),
            @CacheEvict(value = "listingListBySupplier", allEntries = true)
    })
    public ListingResponse updateStock(ListingRequest request) {
        BookListing listing = findListingOrThrow(request.listingId());
        User requester =  SecurityUtils.getCurrentUser();
        requireOwner(listing, requester);

        listing.setStockQuantity(request.stockQuantity());
        return toResponse(bookListingRepository.save(listing));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "listingList", allEntries = true),
            @CacheEvict(value = "listingListBySupplier", allEntries = true)
    })
    public void deleteListing(Long listingId) {
        User requester =  SecurityUtils.getCurrentUser();
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