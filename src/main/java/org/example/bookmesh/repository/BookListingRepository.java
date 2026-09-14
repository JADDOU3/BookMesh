package org.example.bookmesh.repository;

import org.example.bookmesh.model.BookListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookListingRepository extends JpaRepository<BookListing, Long> {

    List<BookListing> findByBookId(Long bookId);

    List<BookListing> findBySupplierId(Long supplierId);

    Optional<BookListing> findByBookIdAndSupplierId(Long bookId, Long supplierId);

    boolean existsByBookIdAndSupplierId(Long bookId, Long supplierId);
}