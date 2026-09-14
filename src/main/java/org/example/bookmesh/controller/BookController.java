package org.example.bookmesh.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.BookRequest;
import org.example.bookmesh.dto.BookResponse;
import org.example.bookmesh.model.User;
import org.example.bookmesh.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request,
                                                   @AuthenticationPrincipal User author) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request, author));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> listBooks(@RequestParam(required = false) Long authorId) {
        List<BookResponse> books = authorId != null
                ? bookService.listBooksByAuthor(authorId)
                : bookService.listBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId,
                                                   @Valid @RequestBody BookRequest request,
                                                   @AuthenticationPrincipal User requester) {
        return ResponseEntity.ok(bookService.updateBook(bookId, request, requester));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId, @AuthenticationPrincipal User requester) {
        bookService.deleteBook(bookId, requester);
        return ResponseEntity.noContent().build();
    }
}