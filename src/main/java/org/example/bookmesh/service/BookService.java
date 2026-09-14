package org.example.bookmesh.service;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.BookRequest;
import org.example.bookmesh.dto.BookResponse;
import org.example.bookmesh.exception.ForbiddenOperationException;
import org.example.bookmesh.exception.ResourceNotFoundException;
import org.example.bookmesh.model.Book;
import org.example.bookmesh.model.Role;
import org.example.bookmesh.model.User;
import org.example.bookmesh.repository.BookListingRepository;
import org.example.bookmesh.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookListingRepository bookListingRepository;

    @Transactional
    public BookResponse createBook(BookRequest request, User author) {
        requireRole(author, Role.AUTHOR);

        Book book = Book.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .author(author)
                .build();

        return toResponse(bookRepository.save(book));
    }

    public BookResponse getBook(Long bookId) {
        return toResponse(findBookOrThrow(bookId));
    }

    public List<BookResponse> listBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<BookResponse> listBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest request, User requester) {
        Book book = findBookOrThrow(bookId);
        requireOwner(book, requester);

        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setPrice(request.price());

        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long bookId, User requester) {
        Book book = findBookOrThrow(bookId);
        requireOwner(book, requester);
        bookListingRepository.deleteAll(bookListingRepository.findByBookId(bookId));
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No book found with id " + bookId));
    }

    private void requireOwner(Book book, User requester) {
        if (!book.getAuthor().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException("You do not own this book");
        }
    }

    private void requireRole(User user, Role role) {
        if (user.getRole() != role) {
            throw new ForbiddenOperationException("Only a " + role.name().toLowerCase() + " can perform this action");
        }
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getAuthor().getId(),
                book.getAuthor().getFullName()
        );
    }
}