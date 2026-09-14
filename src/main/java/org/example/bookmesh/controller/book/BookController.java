package org.example.bookmesh.controller.book;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.book.BookRequest;
import org.example.bookmesh.dto.book.BookResponse;
import org.example.bookmesh.service.book.BookService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @MutationMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public BookResponse createBook(@Argument BookRequest request) {
        return bookService.createBook(request);
    }

    @QueryMapping
    public List<BookResponse> listBooks(@Argument Long authorId) {
        List<BookResponse> books = authorId != null
                ? bookService.listBooksByAuthor(authorId)
                : bookService.listBooks();
        return books;
    }

    @QueryMapping
    public BookResponse getBook(@Argument Long bookId) {
        return bookService.getBook(bookId);
    }

    @MutationMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public BookResponse updateBook(@Argument BookRequest request) {
        return bookService.updateBook(request);
    }

    @MutationMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public Boolean deleteBook(@Argument Long bookId) {
        bookService.deleteBook(bookId);
        return true;
    }
}