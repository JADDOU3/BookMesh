package org.example.bookmesh.controller.book;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.book.ListingRequest;
import org.example.bookmesh.dto.book.ListingResponse;
import org.example.bookmesh.service.book.BookListingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class BookListingController {

    private final BookListingService bookListingService;

    @MutationMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    public ListingResponse createListing(@Argument ListingRequest request) {
        return bookListingService.createListing(request);
    }

   @QueryMapping
    public List<ListingResponse> listListingsForBook(@Argument Long bookId) {
        return bookListingService.listListingsForBook(bookId);
    }

   @QueryMapping
    public List<ListingResponse> listListingsForSupplier(@Argument Long supplierId) {
        return bookListingService.listListingsForSupplier(supplierId);
    }

    @MutationMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    public ListingResponse updateStock(@Argument ListingRequest request) {
        return bookListingService.updateStock(request);
    }


    @MutationMapping
    @PreAuthorize("hasRole('SUPPLIER')")
    public Boolean deleteListing(@Argument Long listingId) {
        bookListingService.deleteListing(listingId);
        return true;
    }
}