package org.example.bookmesh.service.book;

import lombok.RequiredArgsConstructor;
import org.example.bookmesh.dto.book.BookRequest;
import org.example.bookmesh.dto.book.BookResponse;
import org.example.bookmesh.exception.ForbiddenOperationException;
import org.example.bookmesh.exception.ResourceNotFoundException;
import org.example.bookmesh.model.book.Book;
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
public class BookService {

    private final BookRepository bookRepository;
    private final BookListingRepository bookListingRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "bookList", allEntries = true),
            @CacheEvict(value = "bookListByAuthor", allEntries = true)
    })
    public BookResponse createBook(BookRequest request) {
        User author = SecurityUtils.getCurrentUser();

        Book book = Book.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .author(author)
                .build();

        return toResponse(bookRepository.save(book));
    }

    @Cacheable(value = "books", key="#bookId")
    public BookResponse getBook(Long bookId) {
        return toResponse(findBookOrThrow(bookId));
    }


    @Cacheable(value = "bookList")
    public List<BookResponse> listBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }


    @Cacheable(value = "bookListByAuthor", key="#authorId")
    public List<BookResponse> listBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream().map(this::toResponse).toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "bookList" , allEntries = true),
            @CacheEvict(value = "books" , key = "#request.id"),
            @CacheEvict(value = "bookListByAuthor", allEntries = true)
    })
    public BookResponse updateBook(BookRequest request) {
        User requester = SecurityUtils.getCurrentUser();
        Book book = findBookOrThrow(request.id());
        requireOwner(book, requester);

        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setPrice(request.price());

        return toResponse(bookRepository.save(book));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "bookList", allEntries = true),
            @CacheEvict(value = "books", key = "#bookId"),
            @CacheEvict(value = "bookListByAuthor", allEntries = true)
    })
    public void deleteBook(Long bookId ) {
        User requester = SecurityUtils.getCurrentUser();
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