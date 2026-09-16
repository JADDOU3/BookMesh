package org.example.bookmesh.repository.book;

import org.example.bookmesh.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b join fetch b.author where b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);

    @Override
    @Query("select b from Book b join fetch b.author")
    List<Book> findAll();
}