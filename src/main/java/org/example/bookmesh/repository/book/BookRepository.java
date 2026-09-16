package org.example.bookmesh.repository.book;

import org.example.bookmesh.model.book.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.graphql.data.federation.EntityMapping;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<Book> findByAuthorId(@Param("authorId") Long authorId);

}