package org.example.bookmesh.repository.book;

import org.example.bookmesh.model.book.BookListing;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookListingRepository extends JpaRepository<BookListing, Long> {

    @EntityGraph(attributePaths = {"book", "supplier"})
    List<BookListing> findByBookId(Long bookId);

    @EntityGraph(attributePaths = {"book", "supplier"})
    List<BookListing> findBySupplierId(Long supplierId);

    boolean existsByBookIdAndSupplierId(Long bookId, Long supplierId);
}