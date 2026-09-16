package org.example.bookmesh.repository.book;

import org.example.bookmesh.model.book.BookListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookListingRepository extends JpaRepository<BookListing, Long> {

    @Query("select l from BookListing l join fetch l.book join fetch l.supplier where l.book.id = :bookId")
    List<BookListing> findByBookId(@Param("bookId") Long bookId);

    @Query("select l from BookListing l join fetch l.book join fetch l.supplier where l.supplier.id = :supplierId")
    List<BookListing> findBySupplierId(@Param("supplierId") Long supplierId);

    boolean existsByBookIdAndSupplierId(Long bookId, Long supplierId);
}